package be.vlaanderen.vip.magda.client.correlation;

import java.util.Optional;
import java.util.UUID;

public class StaticCorrelationHeaderProvider implements CorrelationHeaderProvider {
    private String correlationId;
    
    StaticCorrelationHeaderProvider(
            String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public Optional<String> getXCorrelationId() {
        return Optional.of(correlationId);
    }
    
    public static CorrelationHeaderProvider create() {
        return new StaticCorrelationHeaderProvider(UUID.randomUUID().toString());
    }
    
    public static CorrelationHeaderProvider create(String correlationId) {
        return new StaticCorrelationHeaderProvider(correlationId);
    }

}
