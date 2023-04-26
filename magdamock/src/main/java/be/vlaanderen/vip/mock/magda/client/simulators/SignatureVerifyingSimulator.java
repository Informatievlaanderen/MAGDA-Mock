package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

public class SignatureVerifyingSimulator extends SOAPSimulator {

    private final ISOAPSimulator simulator;
    private final DocumentSignatureVerifier requestVerifier;

    public SignatureVerifyingSimulator(ResourceFinder finder, ISOAPSimulator simulator, DocumentSignatureVerifier requestVerifier) {
        super(finder);
        this.simulator = simulator;
        this.requestVerifier = requestVerifier;
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        try {
            requestVerifier.verifyDocument(request);

            return simulator.send(request);
        } catch (InvalidSignatureException e) {
            return makeFaultDocument("Server", "ERR_025: Verification Failure: " + e.getMessage());
        }
    }
}
