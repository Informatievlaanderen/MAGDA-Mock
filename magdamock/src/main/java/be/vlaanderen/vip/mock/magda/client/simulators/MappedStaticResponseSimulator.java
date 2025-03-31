package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedStaticResponseSimulator extends BaseStaticResponseSimulator {

    private final Map<List<String>, MagdaDocument> map = new HashMap<>();

    public MappedStaticResponseSimulator(String... keys) {
        super(Arrays.asList(keys));
    }

    public MappedStaticResponseSimulator add(MagdaDocument responseBody, String... values) {
        map.put(Arrays.asList(values), responseBody);

        return this;
    }

    @Override
    @Nullable
    protected MagdaDocument loadResource(String serviceName, String serviceVersion, List<String> values) {
        return map.get(values);
    }
}
