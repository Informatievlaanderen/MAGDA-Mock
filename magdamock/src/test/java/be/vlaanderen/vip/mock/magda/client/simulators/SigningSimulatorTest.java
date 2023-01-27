package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.SneakyThrows;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SigningSimulatorTest {

    @Test
    @SneakyThrows
    void signsResponse() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        var childSimulator = mock(ISOAPSimulator.class);
        when(childSimulator.send(request)).thenReturn(response);
        var signer = mock(DocumentSigner.class);

        var signingSimulator = new SigningSimulator(childSimulator, signer);

        var doc = signingSimulator.send(request);

        assertEquals(response, doc);
        Mockito.verify(signer, times(1)).signDocument(response);
    }

    @Test
    @SneakyThrows
    void throwsMagdaSendFailed_ifSignerFails() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        var childSimulator = mock(ISOAPSimulator.class);
        when(childSimulator.send(request)).thenReturn(response);
        var signer = mock(DocumentSigner.class);
        doThrow(WSSecurityException.class).when(signer).signDocument(response);

        var signingSimulator = new SigningSimulator(childSimulator, signer);

        assertThrows(MagdaSendFailed.class, () -> signingSimulator.send(request));
    }
}