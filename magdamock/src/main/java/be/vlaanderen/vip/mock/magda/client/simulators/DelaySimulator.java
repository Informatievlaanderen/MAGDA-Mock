package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class DelaySimulator implements SOAPSimulator {

    private final SOAPSimulator simulator;
    private final Duration delay;

    public DelaySimulator(SOAPSimulator simulator, Duration delay) {
        this.simulator = simulator;
        this.delay = delay;
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaMockException {
        try {
            var millis = delay.toMillis();

            log.debug("Sleeping for %sms to simulate response delay...".formatted(millis));
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            log.error("Thread was interrupted during sleep period of simulated delay.", ex);
            Thread.currentThread().interrupt();
        }

        return simulator.send(request);
    }
}
