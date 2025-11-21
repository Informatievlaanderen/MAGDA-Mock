package be.vlaanderen.vip.mock.starter.connector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MagdaConnectorBuilder {

    private MagdaConnectorBuilder() {
    }

    public static MagdaConnectorBuilder builder() {
        return new MagdaConnectorBuilder();
    }

    public MockMagdaConnectorBuilder mock() {
        log.debug("Creating a mock magda connector");
        return new MockMagdaConnectorBuilder();
    }

    public MockMagdaRestConnectorBuilder mockRest() {
        log.debug("Creating a mock magda rest connector");
        return new MockMagdaRestConnectorBuilder();
    }

    public RemoteMagdaConnectionBuilder remote() {
        log.debug("Creating a remote magda connector");
        return new RemoteMagdaConnectionBuilder();
    }
}
