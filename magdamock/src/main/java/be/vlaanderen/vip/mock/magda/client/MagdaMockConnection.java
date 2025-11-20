package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;

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

        return send(xml);
    }

    @Override
    public JsonNode sendRestRequest(MagdaRestRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public JsonNode sendRestRequest(String path, String query, String method, String requestBody) {
        throw new NotImplementedException();
    }

    private Document send(Document xml) {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
