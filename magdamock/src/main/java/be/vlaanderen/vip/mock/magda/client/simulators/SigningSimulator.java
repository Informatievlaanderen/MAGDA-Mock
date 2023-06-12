package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import org.apache.wss4j.common.ext.WSSecurityException;

public class SigningSimulator extends BaseSOAPSimulator {

    private final SOAPSimulator simulator;
    private final DocumentSigner responseSigner;

    public SigningSimulator(ResourceFinder finder, SOAPSimulator simulator, DocumentSigner responseSigner) {
        super(finder);
        this.simulator = simulator;
        this.responseSigner = responseSigner;
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaMockException {
        var response = simulator.send(request);

        try {
            responseSigner.signDocument(response.getXml());
        } catch (WSSecurityException e) {
            throw new MagdaMockException("Failed to sign simulated SOAP response", e);
        }

        return response;
    }
}
