package be.vlaanderen.vip.mock.magda.client;

import org.w3c.dom.Document;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {

    private Document defaultResponse = null;
    
    private SOAPSimulator simulator;

    MagdaMockConnection(SOAPSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        log.info("Answering using MAGDA Mock");

        if (defaultResponse != null) {
            Document answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        return send(xml);
    }

    private Document send(Document xml) throws MagdaSendFailed {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
    
    public static MagdaConnection create() {
        return create(SOAPSimulatorBuilder.builder(ResourceFinders.magdaSimulator())
                                          .magdaMockSimulator()
                                          .build());
    }
    
    public static MagdaConnection create(SOAPSimulator simulator) {
        return new MagdaMockConnection(simulator);
    }
}
