package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CombinedSimulatorTest {

    private MagdaDocument mockRequestOf(String service, String version) {
        var request = mock(MagdaDocument.class);
        when(request.getServiceIdentification()).thenReturn(new MagdaServiceIdentification(service, version));

        return request;
    }

    @SneakyThrows
    private SOAPSimulator mockSimulator(MagdaDocument request, MagdaDocument response) {
        var childSimulator = mock(SOAPSimulator.class);
        when(childSimulator.send(request)).thenReturn(response);

        return childSimulator;
    }

    @Test
    @SneakyThrows
    void selectsSimulatorByServiceAndVersion() {
        var requestA = mockRequestOf("foo", "1");
        var responseA = mock(MagdaDocument.class);
        var simulatorA = mockSimulator(requestA, responseA);

        var requestB = mockRequestOf("foo", "2");
        var responseB = mock(MagdaDocument.class);
        var simulatorB = mockSimulator(requestB, responseB);

        var requestC = mockRequestOf("bar", "1");
        var responseC = mock(MagdaDocument.class);
        var simulatorC = mockSimulator(requestC, responseC);

        var combinedSimulator = new CombinedSimulator();
        combinedSimulator.register("foo", "1", simulatorA)
                         .register("foo", "2", simulatorB)
                         .register("bar", "1", simulatorC);

        assertEquals(responseA, combinedSimulator.send(requestA));
        assertEquals(responseB, combinedSimulator.send(requestB));
        assertEquals(responseC, combinedSimulator.send(requestC));
    }

    @Test
    @SneakyThrows
    void throwsMagdaSendFailed_ifNoAccordingSimulatorIsRegistered() {
        var requestA = mockRequestOf("foo", "1");
        var response = mock(MagdaDocument.class);
        var simulator = mockSimulator(requestA, response);

        var requestB = mockRequestOf("bar", "2");

        var combinedSimulator = new CombinedSimulator();
        combinedSimulator.register("foo", "1", simulator);

        assertDoesNotThrow(() -> combinedSimulator.send(requestA));
        assertThrows(MagdaMockException.class, () -> combinedSimulator.send(requestB));
    }
}