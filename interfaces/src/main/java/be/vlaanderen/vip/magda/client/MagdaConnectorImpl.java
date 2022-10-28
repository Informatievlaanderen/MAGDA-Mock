package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.exception.BackendUitzonderingenException;
import be.vlaanderen.vip.magda.exception.GeenAntwoordException;
import be.vlaanderen.vip.magda.legallogging.model.Annotatie;
import be.vlaanderen.vip.magda.legallogging.model.GefaaldeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GeslaagdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.MagdaAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.OnbeantwoordeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.magda.legallogging.model.Uitzondering;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
public class MagdaConnectorImpl implements MagdaConnector {
    private final MagdaConnection connection;
    private final AfnemerLogService afnemerLogService;
    private final MagdaEndpoints magdaEndpoints;
    private final MagdaHoedanigheidService magdaHoedanigheidService;

    @Override
    public MagdaAntwoord send(Aanvraag aanvraag, MagdaDocument request) {
        long start = System.nanoTime();

        final String endpoint = magdaEndpoints.magdaUrl(aanvraag.magdaService());
        logAanvraag(aanvraag);

        MagdaHoedanigheid magdaHoedanigheid = magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie());
        aanvraag.fillIn(request, magdaHoedanigheid);

        log.info(">> Oproep naar {} met referte [{}] en request {}", endpoint, aanvraag.getRequestId(), request);

        MagdaDocument response = callMagda(aanvraag, request);
        Duration duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);

        if (response != null) {
            MagdaAntwoord antwoord = maakAntwoord(aanvraag, response);

            final List<Uitzondering> uitzonderingen = antwoord.getUitzonderingen();
            legalLogging(aanvraag, duration, uitzonderingen, antwoord.getInsz());

            final List<Uitzondering> antwoordUitzonderingen = antwoord.getAntwoordUitzonderingen();
            String uitzonderingenMessage1 = uitzonderingenMessage(uitzonderingen, antwoordUitzonderingen);
            log.debug("<< Antwoord van {} ({} ms) {}", endpoint, duration.toMillis(), uitzonderingenMessage1);

            if (Boolean.FALSE.equals(antwoord.isHeeftInhoud()) && CollectionUtils.isEmpty(antwoordUitzonderingen) && CollectionUtils.isEmpty(uitzonderingen)) {
                throw new BackendUitzonderingenException(aanvraag.getInsz(), getNiveau1Uitzondering(response));
            }

            if (log.isDebugEnabled()) {
                log.info("[{}] {}", aanvraag.getRequestId(), antwoord.getDocument());
            }

            return antwoord;
        }

        logGeenAntwoord(aanvraag);
        log.warn("<< Antwoord van {} ({} ms) TIMEOUT", endpoint, duration.toMillis());
        throw new GeenAntwoordException(aanvraag, "Geen antwoord");
    }


    private List<Uitzondering> getNiveau1Uitzondering(MagdaDocument response) {
        Uitzondering niveau1 = Uitzondering.builder()
                .identificatie("SOAP FAULT")
                .oorsprong("MAGDA")
                .diagnose(getSoap(response))
                .uitzonderingType(TypeUitzondering.FOUT)
                .build();
        return Collections.singletonList(niveau1);
    }

    private String getSoap(MagdaDocument response) {
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(response.getXml()), new StreamResult(writer));
            String rawReturn = writer.toString();
            return rawReturn
                    .replaceAll("\r\n", "")
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
                aanvraag.magdaService().getNaam(),
                aanvraag.magdaService().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }

    private void logAanvraag(Aanvraag aanvraag) {
        afnemerLogService.logAanvraag(new MagdaAanvraag(aanvraag.getInsz(),
                aanvraag.getOverWie(),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                aanvraag.magdaService().getNaam(),
                aanvraag.magdaService().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));

    }

    private void logAlleInszGeslaagd(Aanvraag aanvraag, Duration duration, Set<String> inszs) {
        afnemerLogService.logGeslaagdeAanvraag(new GeslaagdeAanvraag(aanvraag.getInsz(),
                new ArrayList<>(inszs),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                duration,
                aanvraag.magdaService().getNaam(),
                aanvraag.magdaService().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }

    private void logAlleUitzonderingen(Aanvraag aanvraag, Duration duration, List<Uitzondering> uitzonderingen) {
        afnemerLogService.logGefaaldeAanvraag(new GefaaldeAanvraag(aanvraag.getInsz(),
                aanvraag.getCorrelationId(),
                aanvraag.getRequestId(),
                duration,
                uitzonderingen,
                aanvraag.magdaService().getNaam(),
                aanvraag.magdaService().getVersie(),
                magdaHoedanigheidService.getDomeinService(aanvraag.getRegistratie())));
    }


    private String uitzonderingenMessage(List<Uitzondering> uitzonderingen, List<Uitzondering> antwoordUitzonderingen) {
        String uitzonderingenMessage1 = "Ok";
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
        NodeList nodes = response.xpath("//*[local-name()='Body']/*");
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
        NodeList uitzonderingen = response.xpath(expression);
        if (uitzonderingen.getLength() == 1) {
            return alleUitzonderingenIn(uitzonderingen.item(0).getChildNodes());
        } else {
            return Collections.emptyList();
        }
    }

    private boolean responseHeeftInhoud(MagdaDocument response) {
        NodeList inhoud = response.xpath("//Repliek/Antwoorden/Antwoord/Inhoud");
        return inhoud.getLength() == 1;
    }

    private MagdaDocument callMagda(Aanvraag aanvraag, MagdaDocument request) {
        try {
            final Document xml = request.getXml();
            Document response = connection.sendDocument(xml);
            if (response != null) {
                return new MagdaDocument(response);
            }
        } catch (Exception e) {
            final String endpoint = magdaEndpoints.magdaUrl(aanvraag.magdaService());
            log.error("Fout in communicatie met {}", endpoint, e);
        }
        return null;
    }


    private String formatUitzonderingen(String title, List<Uitzondering> uitzonderingen) {
        if (uitzonderingen.isEmpty()) {
            return "";
        }
        final String collect = uitzonderingen.stream()
                .map(uitzondering -> String.format("{%s %s %s '%s'}", uitzondering.getOorsprong(), uitzondering.getUitzonderingType().toString(), uitzondering.getIdentificatie(), uitzondering.getDiagnose()))
                .collect(joining(", "));
        return title + collect;
    }


    private Set<String> vindAlleINSZIn(Aanvraag aanvraag, MagdaDocument antwoord) {
        Set<String> inszs = new HashSet<>();
        inszs.add(aanvraag.getOverWie());
        NodeList nodes = antwoord.xpath("//INSZ");
        if (nodes.getLength() > 0) {
            for (int pos = 0; pos < nodes.getLength(); pos++) {
                inszs.add(nodes.item(pos).getTextContent());
            }
        }
        return inszs;
    }

    private List<Uitzondering> alleUitzonderingenIn(NodeList nodes) {
        final ArrayList<Uitzondering> uitzonderingen = new ArrayList<>();
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            final Node uitzondering = nodes.item(pos);
            if ("Uitzondering".equalsIgnoreCase(uitzondering.getLocalName())) {
                uitzonderingen.add(parseUitzondering(uitzondering));
            }
        }
        return uitzonderingen;
    }

    private Uitzondering parseUitzondering(Node item) {
        final Uitzondering.UitzonderingBuilder builder = Uitzondering.builder();
        builder.annotaties(Collections.emptyList());

        final NodeList nodes = item.getChildNodes();
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            Node child = nodes.item(pos);
            String name = child.getLocalName();
            if (name != null) {
                String value = child.getTextContent();
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

        final NodeList nodes = item.getChildNodes();
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            Node child = nodes.item(pos);
            String name = child.getLocalName();
            if ("Annotatie".equalsIgnoreCase(name)) {
                annotaties.add(parseAnnotatie(child));
            }
        }
        return annotaties;
    }

    private Annotatie parseAnnotatie(Node item) {
        final Annotatie.AnnotatieBuilder builder = Annotatie.builder();
        NodeList nodes = item.getChildNodes();
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            Node child = nodes.item(pos);
            String name = child.getLocalName();
            if (name != null) {
                String value = child.getTextContent();
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
