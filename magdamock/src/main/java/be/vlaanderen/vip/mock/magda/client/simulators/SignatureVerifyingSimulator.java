package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;

public class SignatureVerifyingSimulator extends SOAPSimulator {

    private final ISOAPSimulator simulator;
    private final DocumentSignatureVerifier requestVerifier;

    public SignatureVerifyingSimulator(ISOAPSimulator simulator, DocumentSignatureVerifier requestVerifier) {
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
