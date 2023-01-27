package be.vlaanderen.vip.magda.tester.tests;

import be.vlaanderen.vip.magda.tester.config.TesterConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MockServerTest {

    @Autowired
    protected TesterConfig testerConfig;

    public boolean somebodyListeningOn(String host, int port) {
        boolean ret = false;
        try {
            Socket s = new Socket(host, port);
            ret = true;
            s.close();
        } catch (Exception e) {
            //
        }
        return ret;
    }

    protected void assertServiceAvailable() throws InterruptedException {
        for(int attempt = 1; attempt <= testerConfig.getProbeRetryMax(); attempt++) {
            log.debug("Checking if MagdaMock service is running (" + attempt + "/" + testerConfig.getProbeRetryMax() + ")");
            if(somebodyListeningOn(testerConfig.getServiceHost(), testerConfig.getServicePort())) {
                log.debug("MagdaMock service available.");
                return;
            }

            Thread.sleep(testerConfig.getProbeRetryMils().toMillis());
        }

        log.debug("MagdaMock service unavailable.");
        fail("MagdaMock service unavailable.");
    }
}
