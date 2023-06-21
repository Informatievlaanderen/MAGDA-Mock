package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.util.XmlUtils;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.exception.NoResponseException;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.magda.legallogging.model.*;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.util.CollectionUtils;
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
    public MagdaResponse send(MagdaRequest magdaRequest) {
        var start = System.nanoTime();

        logRequest(magdaRequest);

        var magdaRegistrationInfo = magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration());
        var requestDocument = magdaRequest.toMagdaDocument(magdaRegistrationInfo);

        magdaRequestLoggingEventBuilder(log, Level.INFO, magdaRequest)
                .log("Request to MAGDA service with reference [{}]", magdaRequest.getRequestId());

        magdaRequestLoggingEventBuilder(log, Level.DEBUG, magdaRequest)
                .log("Request: {}", requestDocument);

        try {
            var response = callMagda(requestDocument);
            magdaRequestLoggingEventBuilder(log, Level.INFO, magdaRequest)
                    .log("Response: {}", response);

            var duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);

            var antwoord = maakAntwoord(magdaRequest, response);

            final var uitzonderingen = antwoord.getUitzonderingen();
            legalLogging(magdaRequest, duration, uitzonderingen, antwoord.getInsz());

            final var antwoordUitzonderingen = antwoord.getAntwoordUitzonderingen();
            var uitzonderingenMessage1 = uitzonderingenMessage(uitzonderingen, antwoordUitzonderingen);

            magdaRequestLoggingEventBuilder(log, Level.INFO, magdaRequest)
                    .log("Result of request to MAGDA service with reference [{}] ({} ms): {}", magdaRequest.getRequestId(), duration.toMillis(), uitzonderingenMessage1);

            if(!antwoord.isHeeftInhoud() && CollectionUtils.isEmpty(antwoordUitzonderingen) && CollectionUtils.isEmpty(uitzonderingen)) {
                throw new UitzonderingenSectionInResponseException(magdaRequest.getInsz(), getNiveau1Uitzondering(response));
            }

            return antwoord;
        } catch (MagdaConnectionException e) {
            logNoResponse(magdaRequest);

            throw new NoResponseException("No response", e, magdaRequest);
        }
    }

    private List<UitzonderingEntry> getNiveau1Uitzondering(MagdaDocument response) {
        var niveau1 = UitzonderingEntry.builder()
                .identificatie("SOAP FAULT")
                .oorsprong("MAGDA")
                .diagnose(getSoap(response))
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

    private void logNoResponse(MagdaRequest magdaRequest) {
        clientLogService.logUnansweredRequest(new UnansweredLoggedRequest(magdaRequest.getInsz(),
                magdaRequest.getOverWie(),
                magdaRequest.getCorrelationId(),
                magdaRequest.getRequestId(),
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }

    private void logRequest(MagdaRequest magdaRequest) {
        clientLogService.logMagdaRequest(new MagdaLoggedRequest(magdaRequest.getInsz(),
                magdaRequest.getOverWie(),
                magdaRequest.getCorrelationId(),
                magdaRequest.getRequestId(),
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));

    }

    private void logAlleInszGeslaagd(MagdaRequest magdaRequest, Duration duration, Set<String> inszs) {
        clientLogService.logSucceededRequest(new SucceededLoggedRequest(magdaRequest.getInsz(),
                new ArrayList<>(inszs),
                magdaRequest.getCorrelationId(),
                magdaRequest.getRequestId(),
                duration,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }

    private void logAlleUitzonderingen(MagdaRequest magdaRequest, Duration duration, List<UitzonderingEntry> uitzonderingen) {
        clientLogService.logFailedRequest(new FailedLoggedRequest(magdaRequest.getInsz(),
                magdaRequest.getCorrelationId(),
                magdaRequest.getRequestId(),
                duration,
                uitzonderingen,
                magdaRequest.magdaServiceIdentification().getName(),
                magdaRequest.magdaServiceIdentification().getVersion(),
                magdaHoedanigheidService.getDomeinService(magdaRequest.getRegistration())));
    }


    private String uitzonderingenMessage(List<UitzonderingEntry> uitzonderingen, List<UitzonderingEntry> antwoordUitzonderingen) {
        var uitzonderingenMessage1 = "Ok";
        if (!antwoordUitzonderingen.isEmpty() || !uitzonderingen.isEmpty()) {
            uitzonderingenMessage1 = formatUitzonderingen("Level 2: ", uitzonderingen) + formatUitzonderingen("Level 3: ", antwoordUitzonderingen);
        }
        return uitzonderingenMessage1;
    }

    private void legalLogging(MagdaRequest magdaRequest, Duration duration, List<UitzonderingEntry> uitzonderingen, Set<String> alleInsz) {
        if (uitzonderingen.isEmpty()) {
            logAlleInszGeslaagd(magdaRequest, duration, alleInsz);
        } else {
            logAlleUitzonderingen(magdaRequest, duration, uitzonderingen);
        }
    }

    private MagdaResponse maakAntwoord(MagdaRequest magdaRequest, MagdaDocument response) {
        return MagdaResponse.builder()
                .correlationId(magdaRequest.getCorrelationId())
                .requestId(magdaRequest.getRequestId())
                .uitzonderingen(level1Uitzonderingen(response))
                .antwoordUitzonderingen(level2Uitzonderingen(response))
                .body(getBody(response))
                .document(response)
                .heeftInhoud(responseHeeftInhoud(response))
                .insz(vindAlleINSZIn(magdaRequest, response))
                .build();
    }

    private Node getBody(MagdaDocument response) {
        var nodes = response.xpath("//*[local-name()='Body']/*");
        if (nodes.getLength() == 1) {
            return nodes.item(0);
        }
        return null;
    }

    private List<UitzonderingEntry> level1Uitzonderingen(MagdaDocument response) {
        return parseUitzonderingen(response, "//Repliek/Uitzonderingen");
    }

    private List<UitzonderingEntry> level2Uitzonderingen(MagdaDocument response) {
        return parseUitzonderingen(response, "//Repliek/Antwoorden/Antwoord/Uitzonderingen");
    }

    private List<UitzonderingEntry> parseUitzonderingen(MagdaDocument response, String expression) {
        var uitzonderingen = response.xpath(expression);
        if (uitzonderingen.getLength() == 1) {
            return alleUitzonderingenIn(uitzonderingen.item(0).getChildNodes());
        } else {
            return Collections.emptyList();
        }
    }

    private boolean responseHeeftInhoud(MagdaDocument response) {
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


    private String formatUitzonderingen(String title, List<UitzonderingEntry> uitzonderingen) {
        if (uitzonderingen.isEmpty()) {
            return "";
        }
        final var collect = uitzonderingen.stream()
                .map(uitzonderingEntry -> String.format("{%s %s %s '%s'}", uitzonderingEntry.getOorsprong(), uitzonderingEntry.getUitzonderingType().toString(), uitzonderingEntry.getIdentificatie(), uitzonderingEntry.getDiagnose()))
                .collect(joining(", "));
        return title + collect;
    }


    private Set<String> vindAlleINSZIn(MagdaRequest magdaRequest, MagdaDocument antwoord) {
        Set<String> inszs = new HashSet<>();
        inszs.add(magdaRequest.getOverWie());
        var nodes = antwoord.xpath("//INSZ");
        if (nodes.getLength() > 0) {
            for (var pos = 0; pos < nodes.getLength(); pos++) {
                inszs.add(nodes.item(pos).getTextContent());
            }
        }
        return inszs;
    }

    private List<UitzonderingEntry> alleUitzonderingenIn(NodeList nodes) {
        final var uitzonderingen = new ArrayList<UitzonderingEntry>();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            final var uitzondering = nodes.item(pos);
            if ("Uitzondering".equalsIgnoreCase(uitzondering.getLocalName())) {
                uitzonderingen.add(parseUitzondering(uitzondering));
            }
        }
        return uitzonderingen;
    }

    private UitzonderingEntry parseUitzondering(Node item) {
        final var builder = UitzonderingEntry.builder();
        builder.annotaties(Collections.emptyList());

        final var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if (name != null) {
                var value = child.getTextContent();
                if ("Identificatie".equalsIgnoreCase(name)) {
                    builder.identificatie(value);
                } else if ("Oorsprong".equalsIgnoreCase(name)) {
                    builder.oorsprong(value);
                } else if ("Type".equalsIgnoreCase(name)) {
                    builder.uitzonderingType(UitzonderingType.valueOf(value));
                } else if ("Diagnose".equalsIgnoreCase(name)) {
                    builder.diagnose(value);
                } else if ("Annotaties".equalsIgnoreCase(name)) {
                    builder.annotaties(parseAnnotaties(child));
                } else if ("Uitzondering".equalsIgnoreCase(name)) {
                    return parseUitzondering(child);
                }
            }
        }
        return builder.build();
    }

    private List<AnnotatieField> parseAnnotaties(Node item) {
        List<AnnotatieField> annotaties = new ArrayList<>();

        final var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if ("Annotatie".equalsIgnoreCase(name)) {
                annotaties.add(parseAnnotatie(child));
            }
        }
        return annotaties;
    }

    private AnnotatieField parseAnnotatie(Node item) {
        final var builder = AnnotatieField.builder();
        var nodes = item.getChildNodes();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            var child = nodes.item(pos);
            var name = child.getLocalName();
            if (name != null) {
                var value = child.getTextContent();
                if ("Naam".equalsIgnoreCase(name)) {
                    builder.naam(value);
                } else if ("Waarde".equalsIgnoreCase(name)) {
                    builder.waarde(value);
                }
            }
        }
        return builder.build();
    }
}
