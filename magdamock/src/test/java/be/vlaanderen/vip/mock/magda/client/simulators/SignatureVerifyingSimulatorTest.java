package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SignatureVerifyingSimulatorTest {

    @Test
    @SneakyThrows
    void verifiesRequest() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        var childSimulator = mock(ISOAPSimulator.class);
        when(childSimulator.send(request)).thenReturn(response);
        var verifier = mock(DocumentSignatureVerifier.class);

        var signingSimulator = new SignatureVerifyingSimulator(childSimulator, verifier);

        var doc = signingSimulator.send(request);

        assertEquals(response, doc);
        Mockito.verify(verifier, times(1)).verifyDocument(request);
    }

    @Test
    @SneakyThrows
    void returnsFaultDocument_ifVerifierFails() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        var childSimulator = mock(ISOAPSimulator.class);
        when(childSimulator.send(request)).thenReturn(response);
        var verifier = mock(DocumentSignatureVerifier.class);
        doThrow(new InvalidSignatureException("Huh?!")).when(verifier).verifyDocument(request);

        var signingSimulator = new SignatureVerifyingSimulator(childSimulator, verifier);

        var doc = signingSimulator.send(request);

        assertEquals("Server",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
        assertEquals("ERR_025: Verification Failure: Huh?!",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultstring"));
    }
}