package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import lombok.SneakyThrows;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SigningSimulatorTest {
    @Mock private SOAPSimulator childSimulator;
    @Mock private DocumentSigner signer;

    @InjectMocks
    private SigningSimulator signingSimulator;
    
    @Test
    @SneakyThrows
    void signsResponse() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        when(childSimulator.send(request)).thenReturn(response);

        var doc = signingSimulator.send(request);

        assertEquals(response, doc);
        Mockito.verify(signer, times(1)).signDocument(response.getXml());
    }

    @Test
    @SneakyThrows
    void throwsMagdaSendFailed_ifSignerFails() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        var responseXml = mock(Document.class);
        when(response.getXml()).thenReturn(responseXml);
        when(childSimulator.send(request)).thenReturn(response);
        doThrow(WSSecurityException.class).when(signer).signDocument(responseXml);

        assertThrows(MagdaMockException.class, () -> signingSimulator.send(request));
    }
}