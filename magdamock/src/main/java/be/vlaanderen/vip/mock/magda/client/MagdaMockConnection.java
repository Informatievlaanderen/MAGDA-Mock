package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {

    private Document defaultResponse = null;
    
    private final SOAPSimulator simulator;

    MagdaMockConnection(SOAPSimulator simulator) {
        this.simulator = simulator;
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

    private Document send(Document xml) {
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
