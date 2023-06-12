package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignatureVerifyingSimulatorTest {
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
        Mockito.verify(verifier, times(1)).verifyDocument(request.getXml());
    }

    @Test
    @SneakyThrows
    void returnsFaultDocument_ifVerifierFails() {
        var request = mock(MagdaDocument.class);
        var requestXml = mock(Document.class);
        when(request.getXml()).thenReturn(requestXml);
        doThrow(new InvalidSignatureException("Huh?!")).when(verifier).verifyDocument(requestXml);

        var doc = signingSimulator.send(request);

        assertEquals("Server",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
        assertEquals("ERR_025: Verification Failure: Huh?!",
                doc.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultstring"));
    }
}