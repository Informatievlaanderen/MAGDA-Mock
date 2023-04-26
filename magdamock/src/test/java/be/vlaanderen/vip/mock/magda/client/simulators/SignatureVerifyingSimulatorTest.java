package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignatureVerifyingSimulatorTest {
    @Mock private ResourceFinder finder;
    @Mock private SOAPSimulator childSimulator;
    @Mock private DocumentSignatureVerifier verifier;
    
    @InjectMocks
    private SignatureVerifyingSimulator signingSimulator;
    

    @Test
    @SneakyThrows
    void verifiesRequest() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        when(childSimulator.send(request)).thenReturn(response);

        var doc = signingSimulator.send(request);

        assertEquals(response, doc);
        Mockito.verify(verifier, times(1)).verifyDocument(request);
    }

    @Test
    @SneakyThrows
    void returnsFaultDocument_ifVerifierFails() {
        var request = mock(MagdaDocument.class);
        doThrow(new InvalidSignatureException("Huh?!")).when(verifier).verifyDocument(request);

        var doc = signingSimulator.send(request);

        assertEquals("Server",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
        assertEquals("ERR_025: Verification Failure: Huh?!",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultstring"));
    }
}