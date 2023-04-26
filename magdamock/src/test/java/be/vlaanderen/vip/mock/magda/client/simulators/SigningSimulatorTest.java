package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.SneakyThrows;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SigningSimulatorTest {
    @Mock private ResourceFinder finder;
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
        Mockito.verify(signer, times(1)).signDocument(response);
    }

    @Test
    @SneakyThrows
    void throwsMagdaSendFailed_ifSignerFails() {
        var request = mock(MagdaDocument.class);
        var response = mock(MagdaDocument.class);
        when(childSimulator.send(request)).thenReturn(response);
        doThrow(WSSecurityException.class).when(signer).signDocument(response);

        assertThrows(MagdaSendFailed.class, () -> signingSimulator.send(request));
    }
}