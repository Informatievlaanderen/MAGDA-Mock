package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.client.utils.MockDataTemplating;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {

    private final SOAPSimulator simulator;
    private Document defaultResponse = null;

    MagdaMockConnection(SOAPSimulator simulator) {
        this.simulator = simulator;
    }

    public static MagdaConnection create() throws URISyntaxException, IOException {
        return create(SOAPSimulatorBuilder.builder(ResourceFinders.magdaSimulator())
                .magdaMockSimulator()
                .build());
    }

    public static MagdaConnection create(SOAPSimulator simulator) {
        return new MagdaMockConnection(simulator);
    }

    @Override
    public Document sendDocument(Document xml) {
        log.info("Answering using MAGDA Mock");

        if (defaultResponse != null) {
            var answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        LocalDate date;
        try {
            String dateString = MagdaDocument.fromDocument(xml).getValue("//Verzoek/Context/Bericht/Tijdstip/Datum").strip();
            date = LocalDate.parse(dateString);
        } catch (Exception e) {
            log.info("Unable to extract date and time from request");
            date = LocalDate.now();
        }
        String document = MagdaDocument.fromDocument(send(xml)).toString();
        document = MockDataTemplating.processTemplatingValues(document, date);
        return MagdaDocument.fromString(document).getXml();
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(MagdaRestRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(String path, String query, String method, String requestBody, String dateHeader) {
        throw new NotImplementedException();
    }

    private Document send(Document xml) {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
