package be.vlaanderen.vip.magda.client.correlation;

import java.util.Optional;

public interface CorrelationHeaderProvider {

    Optional<String> getXCorrelationId();
}