package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DelaySimulatorTest {

    private static final Duration DELAY_DURATION = Duration.ofMillis(200);

    @Mock
    private SOAPSimulator simulator;
    @Mock
    private MagdaDocument request;
    @Mock
    private MagdaDocument expectedResponse;

    private DelaySimulator delaySimulator;

    @BeforeEach
    void setup() {
        this.delaySimulator = new DelaySimulator(simulator, DELAY_DURATION);
    }

    @Test
    void simulatesDelayOnResponse() {
        when(simulator.send(request)).thenReturn(expectedResponse);

        var stopWatch = new StopWatch();

        stopWatch.start();
        var response = delaySimulator.send(request);
        stopWatch.stop();

        assertEquals(expectedResponse, response);
        assertTrue(stopWatch.getTime(MILLISECONDS) >= DELAY_DURATION.toMillis());
    }

    @Test
    void simulatesDelayOnException() {
        when(simulator.send(request)).thenThrow(MagdaMockException.class);

        var stopWatch = new StopWatch();

        stopWatch.start();
        assertThrows(MagdaMockException.class, () -> delaySimulator.send(request));
        stopWatch.stop();

        assertTrue(stopWatch.getTime(MILLISECONDS) >= DELAY_DURATION.toMillis());
    }
}