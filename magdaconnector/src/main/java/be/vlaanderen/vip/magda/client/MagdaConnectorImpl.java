package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.xml.XmlUtils;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.magda.exception.NoResponseException;
import be.vlaanderen.vip.magda.exception.ServerException;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.legallogging.model.*;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static be.vlaanderen.vip.magda.client.util.LoggingUtils.magdaRequestLoggingEventBuilder;
import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
public class MagdaConnectorImpl implements MagdaConnector {
    private final MagdaConnection connection;
    private final ClientLogService clientLogService;
    private final MagdaHoedanigheidService magdaHoedanigheidService;

    @Override
    public MagdaResponse send(MagdaRequest magdaRequest) throws ServerException {
        return send(magdaRequest, UUID.randomUUID());
    }

    @Override
    public MagdaResponse send(MagdaRequest magdaRequest, UUID requestId) throws ServerException {
        magdaRequest.setCorrelationId(CorrelationId.get());

        try {
            var start = System.nanoTime();

            logRequest(magdaRequest, requestId);

            var magdaRegistrationInfo = magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration());
            var requestDocument = magdaRequest.toMagdaDocument(requestId, magdaRegistrationInfo);

            magdaRequestLoggingEventBuilder(log, Level.INFO, magdaRequest)
                    .log("Request to MAGDA service with reference [{}]", requestId);

            magdaRequestLoggingEventBuilder(log, Level.DEBUG, magdaRequest)
                    .log("Request: {}", requestDocument);

            var response = callMagda(requestDocument);
            magdaRequestLoggingEventBuilder(log, Level.DEBUG, magdaRequest)
                    .log("Response: {}", response);

            var duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);

            var antwoord = buildResponse(magdaRequest, requestId, response);

            final var uitzonderingen = antwoord.getUitzonderingEntries();
            legalLogging(magdaRequest, requestId, duration, uitzonderingen, antwoord.getSubjects());

            final var antwoordUitzonderingen = antwoord.getResponseUitzonderingEntries();

            if(antwoordUitzonderingen.isEmpty() && uitzonderingen.isEmpty()) {
                logRequestResultMessage(Level.INFO, magdaRequest, requestId, duration, "Ok");
            } else {
                logRequestResultMessage(Level.WARN, magdaRequest, requestId, duration, messageForUitzonderingEntries(uitzonderingen, antwoordUitzonderingen));
            }

            if(!antwoord.isHasContents() && antwoordUitzonderingen.isEmpty() && uitzonderingen.isEmpty()) {
                throw new UitzonderingenSectionInResponseException(magdaRequest.getSubject(), getLevel1UitzonderingEntry(response), magdaRequest.magdaServiceIdentification(), magdaRequest.getCorrelationId(), requestId);
            }

            return antwoord;
        } catch (MagdaConnectionException e) {
            logNoResponse(magdaRequest, requestId);

            throw new NoResponseException("No response", e, magdaRequest, requestId, e.getStatusCode());
        } finally {
            CorrelationId.clear();
        }
    }

    private List<UitzonderingEntry> getLevel1UitzonderingEntry(MagdaDocument response) {
        var niveau1 = UitzonderingEntry.builder()
                .identification("SOAP FAULT")
                .origin("MAGDA")
                .diagnosis(getSoap(response))
                .uitzonderingType(UitzonderingType.FOUT)
                .build();
        return Collections.singletonList(niveau1);
    }

    private String getSoap(MagdaDocument response) {
        try {
            var writer = new StringWriter();
            XmlUtils.createTransformer().transform(new DOMSource(response.getXml()), new StreamResult(writer));
            var rawReturn = writer.toString();
            return rawReturn
                    .replace("\r\n", "")
                    .replaceAll("\\s{2,}", " ")
                    .trim();
        } catch (TransformerException e) {
            return "Error during parsing soap response.";
        }
    }

    private void logNoResponse(MagdaRequest magdaRequest, UUID requestId) {
        clientLogService.logUnansweredRequest(new UnansweredLoggedRequest(
                magdaRequest.getSubject(),
                magdaRequest.getCorrelationId(),
                requestId,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }

    private void logRequest(MagdaRequest magdaRequest, UUID requestId) {
        clientLogService.logMagdaRequest(new MagdaLoggedRequest(
                magdaRequest.getSubject(),
                magdaRequest.getCorrelationId(),
                requestId,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));

    }

    private void logAllSubjectsSucceeded(MagdaRequest magdaRequest, UUID requestId, Duration duration, Set<SubjectIdentificationNumber> subjects) {
        clientLogService.logSucceededRequest(new SucceededLoggedRequest(
                new ArrayList<>(subjects),
                magdaRequest.getCorrelationId(),
                requestId,
                duration,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }

    private void logAllUitzonderingEntries(MagdaRequest magdaRequest, UUID requestId, Duration duration, List<UitzonderingEntry> uitzonderingEntries) {
        clientLogService.logFailedRequest(new FailedLoggedRequest(
                magdaRequest.getCorrelationId(),
                requestId,
                duration,
                uitzonderingEntries,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }


    private String messageForUitzonderingEntries(List<UitzonderingEntry> uitzonderingenEntries, List<UitzonderingEntry> responseUitzonderingEntries) {
        return formatUitzonderingEntries("Level 2: ", uitzonderingenEntries) + formatUitzonderingEntries("Level 3: ", responseUitzonderingEntries);
    }

    private void logRequestResultMessage(Level logLevel, MagdaRequest magdaRequest, UUID requestId, Duration duration, String message) {
        magdaRequestLoggingEventBuilder(log, logLevel, magdaRequest)
                .log("Result of request to MAGDA service with reference [{}] ({} ms): {}", requestId, duration.toMillis(), message);
    }

    private void legalLogging(MagdaRequest magdaRequest, UUID requestId, Duration duration, List<UitzonderingEntry> uitzonderingEntries, Set<SubjectIdentificationNumber> subjects) {
        if (uitzonderingEntries.isEmpty()) {
            logAllSubjectsSucceeded(magdaRequest, requestId, duration, subjects);
        } else {
            logAllUitzonderingEntries(magdaRequest, requestId, duration, uitzonderingEntries);
        }
    }

    MagdaResponse buildResponse(MagdaRequest magdaRequest, UUID requestId, MagdaDocument responseDocument) {
        return MagdaResponse.builder()
                .correlationId(magdaRequest.getCorrelationId())
                .requestId(requestId)
                .uitzonderingEntries(level2UitzonderingEntries(responseDocument))
                .responseUitzonderingEntries(level3UitzonderingEntries(responseDocument))
                .body(getBody(responseDocument))
                .document(responseDocument)
                .hasContents(responseHasContents(responseDocument))
                .subjects(findAllSubjectsIn(magdaRequest, responseDocument))
                .build();
    }

    private Node getBody(MagdaDocument responseDocument) {
        var nodes = responseDocument.xpath("//*[local-name()='Body']/*");
        if (nodes.getLength() == 1) {
            return nodes.item(0);
        }
        return null;
    }

    private List<UitzonderingEntry> level2UitzonderingEntries(MagdaDocument response) {
        return parseUitzonderingenSection(response, "//Repliek/Uitzonderingen");
    }

    private List<UitzonderingEntry> level3UitzonderingEntries(MagdaDocument response) {
        return parseUitzonderingenSection(response, "//Repliek/Antwoorden/Antwoord/Uitzonderingen");
    }

    private List<UitzonderingEntry> parseUitzonderingenSection(MagdaDocument response, String expression) {
        var uitzonderingen = response.xpath(expression);
        if (uitzonderingen.getLength() == 1) {
            return allUitzonderingEntriesIn(uitzonderingen.item(0).getChildNodes());
        } else {
            return Collections.emptyList();
        }
    }

    private boolean responseHasContents(MagdaDocument response) {
        var inhoud = response.xpath("//Repliek/Antwoorden/Antwoord/Inhoud");
        return inhoud.getLength() == 1;
    }

    private MagdaDocument callMagda(MagdaDocument request) throws MagdaConnectionException {
        final var xml = request.getXml();
        var response = connection.sendDocument(xml);
        if (response != null) {
            return new MagdaDocument(response);
        } else {
            throw new IllegalStateException("BUG: sendDocument returned null");
        }
    }


    private String formatUitzonderingEntries(String title, List<UitzonderingEntry> uitzonderingen) {
        if (uitzonderingen.isEmpty()) {
            return "";
        }
        final var collect = uitzonderingen.stream()
                .map(uitzonderingEntry -> String.format("{%s %s %s '%s'}", uitzonderingEntry.getOrigin(), uitzonderingEntry.getUitzonderingType().toString(), uitzonderingEntry.getIdentification(), uitzonderingEntry.getDiagnosis()))
                .collect(joining(", "));
        return title + collect;
    }

    private Set<SubjectIdentificationNumber> findAllSubjectsIn(MagdaRequest magdaRequest, MagdaDocument responseDocument) {
        Set<SubjectIdentificationNumber> subjects = new HashSet<>();

        subjects.add(magdaRequest.getSubject());
        subjects.addAll(responseDocument.getValues("//INSZ").stream().map(INSZNumber::of).toList());
        subjects.addAll(responseDocument.getValues("//Ondernemingsnummer").stream().map(KBONumber::of).toList());

        return subjects;
    }

    private List<UitzonderingEntry> allUitzonderingEntriesIn(NodeList nodes) {
        final var uitzonderingen = new ArrayList<UitzonderingEntry>();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            final var uitzondering = nodes.item(pos);
            if ("Uitzondering".equalsIgnoreCase(uitzondering.getLocalName())) {
                uitzonderingen.add(parseUitzonderingEntry(uitzondering));
            }
        }
        return uitzonderingen;
    }

    private UitzonderingEntry parseUitzonderingEntry(Node item) {
        final var builder = UitzonderingEntry.builder();
        builder.annotatieFields(Collections.emptyList());

        final var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if (name != null) {
                var value = child.getTextContent();
                if ("Identificatie".equalsIgnoreCase(name)) {
                    builder.identification(value);
                } else if ("Oorsprong".equalsIgnoreCase(name)) {
                    builder.origin(value);
                } else if ("Type".equalsIgnoreCase(name)) {
                    builder.uitzonderingType(UitzonderingType.valueOf(value));
                } else if ("Diagnose".equalsIgnoreCase(name)) {
                    builder.diagnosis(value);
                } else if ("Annotaties".equalsIgnoreCase(name)) {
                    builder.annotatieFields(parseAnnotatieFields(child));
                } else if ("Uitzondering".equalsIgnoreCase(name)) {
                    return parseUitzonderingEntry(child);
                }
            }
        }
        return builder.build();
    }

    private List<AnnotatieField> parseAnnotatieFields(Node item) {
        List<AnnotatieField> annotaties = new ArrayList<>();

        final var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if ("Annotatie".equalsIgnoreCase(name)) {
                annotaties.add(parseAnnotatieField(child));
            }
        }
        return annotaties;
    }

    private AnnotatieField parseAnnotatieField(Node item) {
        final var builder = AnnotatieField.builder();
        var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if (name != null) {
                var value = child.getTextContent();
                if ("Naam".equalsIgnoreCase(name)) {
                    builder.name(value);
                } else if ("Waarde".equalsIgnoreCase(name)) {
                    builder.value(value);
                }
            }
        }
        return builder.build();
    }
}
