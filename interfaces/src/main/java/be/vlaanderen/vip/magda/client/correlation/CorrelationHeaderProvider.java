package be.vlaanderen.vip.magda.client.correlation;

import java.util.Optional;

/**
 * @deprecated for all the relevant requests, replace all uses of a CorrelationHeaderProvider with request.setCorrelationId(correlationId)`.
 */
@Deprecated(forRemoval = true)
public interface CorrelationHeaderProvider {

    Optional<String> getXCorrelationId();
}