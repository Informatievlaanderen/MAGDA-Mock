package be.vlaanderen.vip.magda.client.correlation;

import java.util.Optional;

public class NullCorrelationHeaderProvider implements CorrelationHeaderProvider {

    private static NullCorrelationHeaderProvider instance;

    public static NullCorrelationHeaderProvider getInstance() {
        if(instance == null) {
            instance = new NullCorrelationHeaderProvider();
        }

        return instance;
    }

    private NullCorrelationHeaderProvider() {}

    @Override
    public Optional<String> getXCorrelationId() {
        return Optional.empty();
    }
}