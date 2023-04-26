package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import org.apache.wss4j.common.ext.WSSecurityException;

public class SigningSimulator extends SOAPSimulator {

    private final ISOAPSimulator simulator;
    private final DocumentSigner responseSigner;

    public SigningSimulator(ResourceFinder finder, ISOAPSimulator simulator, DocumentSigner responseSigner) {
        super(finder);
        this.simulator = simulator;
        this.responseSigner = responseSigner;
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        var response = simulator.send(request);

        try {
            responseSigner.signDocument(response);
        } catch (WSSecurityException e) {
            throw new MagdaSendFailed("Failed to sign simulated SOAP response", e);
        }

        return response;
    }
}
