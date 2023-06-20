package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.util.XmlUtils;
import be.vlaanderen.vip.magda.exception.BackendUitzonderingenException;
import be.vlaanderen.vip.magda.exception.GeenAntwoordException;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.magda.legallogging.model.*;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;
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

import static be.vlaanderen.vip.magda.client.util.LoggingUtils.magdaAanvraagLoggingEventBuilder;
import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
public class MagdaConnectorImpl implements MagdaConnector {
    private final MagdaConnection connection;
    private final AfnemerLogService afnemerLogService;
    private final MagdaHoedanigheidService magdaHoedanigheidService;

    @Override
    public MagdaAntwoord send(Aanvraag aanvraag) {
        var start = System.nanoTime();

        logAanvraag(aanvraag);

        var magdaRegistrationInfo = magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie());
        var requestDocument = aanvraag.toMagdaDocument(magdaRegistrationInfo);

        magdaAanvraagLoggingEventBuilder(log, Level.INFO, aanvraag)
                .log("Aanvraag naar MAGDA dienst met referte [{}]", aanvraag.getRequestId());

        magdaAanvraagLoggingEventBuilder(log, Level.DEBUG, aanvraag)
                .log("Request: {}", requestDocument);

        try {
            var response = callMagda(requestDocument);
            magdaAanvraagLoggingEventBuilder(log, Level.INFO, aanvraag)
                    .log("Response: {}", response);

            var duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);

            var antwoord = maakAntwoord(aanvraag, response);

            final var uitzonderingen = antwoord.getUitzonderingen();
            legalLogging(aanvraag, duration, uitzonderingen, antwoord.getInsz());

            final var antwoordUitzonderingen = antwoord.getAntwoordUitzonderingen();
            var uitzonderingenMessage1 = uitzonderingenMessage(uitzonderingen, antwoordUitzonderingen);

            magdaAanvraagLoggingEventBuilder(log, Level.INFO, aanvraag)
                    .log("Resultaat van aanvraag naar MAGDA dienst met referte [{}] ({} ms): {}",aanvraag.getRequestId(), duration.toMillis(), uitzonderingenMessage1);

            if(!antwoord.isHeeftInhoud() && CollectionUtils.isEmpty(antwoordUitzonderingen) && CollectionUtils.isEmpty(uitzonderingen)) {
                throw new BackendUitzonderingenException(aanvraag.getInsz(), getNiveau1Uitzondering(response));
            }

            return antwoord;
        } catch (MagdaConnectionException e) {
            logGeenAntwoord(aanvraag);

            throw new GeenAntwoordException("No response", e, aanvraag);
        }
    }

    private List<Uitzondering> getNiveau1Uitzondering(MagdaDocument response) {
        var niveau1 = Uitzondering.builder()
                .identificatie("SOAP FAULT")
                .oorsprong("MAGDA")
                .diagnose(getSoap(response))
                .uitzonderingType(TypeUitzondering.FOUT)
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

    private void logGeenAntwoord(Aanvraag aanvraag) {
        afnemerLogService.logOnbeantwoordeAanvraag(new OnbeantwoordeAanvraag(aanvraag.getInsz(),
                aanvraag.getOverWie(),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                aanvraag.magdaServiceIdentification().getNaam(),
                aanvraag.magdaServiceIdentification().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }

    private void logAanvraag(Aanvraag aanvraag) {
        afnemerLogService.logAanvraag(new MagdaAanvraag(aanvraag.getInsz(),
                aanvraag.getOverWie(),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                aanvraag.magdaServiceIdentification().getNaam(),
                aanvraag.magdaServiceIdentification().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));

    }

    private void logAlleInszGeslaagd(Aanvraag aanvraag, Duration duration, Set<String> inszs) {
        afnemerLogService.logGeslaagdeAanvraag(new GeslaagdeAanvraag(aanvraag.getInsz(),
                new ArrayList<>(inszs),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                duration,
                aanvraag.magdaServiceIdentification().getNaam(),
                aanvraag.magdaServiceIdentification().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }

    private void logAlleUitzonderingen(Aanvraag aanvraag, Duration duration, List<Uitzondering> uitzonderingen) {
        afnemerLogService.logGefaaldeAanvraag(new GefaaldeAanvraag(aanvraag.getInsz(),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                duration,
                uitzonderingen,
                aanvraag.magdaServiceIdentification().getNaam(),
                aanvraag.magdaServiceIdentification().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }


    private String uitzonderingenMessage(List<Uitzondering> uitzonderingen, List<Uitzondering> antwoordUitzonderingen) {
        var uitzonderingenMessage1 = "Ok";
        if (!antwoordUitzonderingen.isEmpty() || !uitzonderingen.isEmpty()) {
            uitzonderingenMessage1 = formatUitzonderingen("Level 2: ", uitzonderingen) + formatUitzonderingen("Level 3: ", antwoordUitzonderingen);
        }
        return uitzonderingenMessage1;
    }

    private void legalLogging(Aanvraag aanvraag, Duration duration, List<Uitzondering> uitzonderingen, Set<String> alleInsz) {
        if (uitzonderingen.isEmpty()) {
            logAlleInszGeslaagd(aanvraag, duration, alleInsz);
        } else {
            logAlleUitzonderingen(aanvraag, duration, uitzonderingen);
        }
    }

    private MagdaAntwoord maakAntwoord(Aanvraag aanvraag, MagdaDocument response) {
        return MagdaAntwoord.builder()
                .correlationId(aanvraag.getCorrelationId())
                .requestId(aanvraag.getRequestId())
                .uitzonderingen(level1Uitzonderingen(response))
                .antwoordUitzonderingen(level2Uitzonderingen(response))
                .body(getBody(response))
                .document(response)
                .heeftInhoud(responseHeeftInhoud(response))
                .insz(vindAlleINSZIn(aanvraag, response))
                .build();
    }

    private Node getBody(MagdaDocument response) {
        var nodes = response.xpath("//*[local-name()='Body']/*");
        if (nodes.getLength() == 1) {
            return nodes.item(0);
        }
        return null;
    }

    private List<Uitzondering> level1Uitzonderingen(MagdaDocument response) {
        return parseUitzonderingen(response, "//Repliek/Uitzonderingen");
    }

    private List<Uitzondering> level2Uitzonderingen(MagdaDocument response) {
        return parseUitzonderingen(response, "//Repliek/Antwoorden/Antwoord/Uitzonderingen");
    }

    private List<Uitzondering> parseUitzonderingen(MagdaDocument response, String expression) {
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


    private String formatUitzonderingen(String title, List<Uitzondering> uitzonderingen) {
        if (uitzonderingen.isEmpty()) {
            return "";
        }
        final var collect = uitzonderingen.stream()
                .map(uitzondering -> String.format("{%s %s %s '%s'}", uitzondering.getOorsprong(), uitzondering.getUitzonderingType().toString(), uitzondering.getIdentificatie(), uitzondering.getDiagnose()))
                .collect(joining(", "));
        return title + collect;
    }


    private Set<String> vindAlleINSZIn(Aanvraag aanvraag, MagdaDocument antwoord) {
        Set<String> inszs = new HashSet<>();
        inszs.add(aanvraag.getOverWie());
        var nodes = antwoord.xpath("//INSZ");
        if (nodes.getLength() > 0) {
            for (var pos = 0; pos < nodes.getLength(); pos++) {
                inszs.add(nodes.item(pos).getTextContent());
            }
        }
        return inszs;
    }

    private List<Uitzondering> alleUitzonderingenIn(NodeList nodes) {
        final var uitzonderingen = new ArrayList<Uitzondering>();
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            final var uitzondering = nodes.item(pos);
            if ("Uitzondering".equalsIgnoreCase(uitzondering.getLocalName())) {
                uitzonderingen.add(parseUitzondering(uitzondering));
            }
        }
        return uitzonderingen;
    }

    private Uitzondering parseUitzondering(Node item) {
        final var builder = Uitzondering.builder();
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
                    builder.uitzonderingType(TypeUitzondering.valueOf(value));
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

    private List<Annotatie> parseAnnotaties(Node item) {
        List<Annotatie> annotaties = new ArrayList<>();

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

    private Annotatie parseAnnotatie(Node item) {
        final var builder = Annotatie.builder();
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
