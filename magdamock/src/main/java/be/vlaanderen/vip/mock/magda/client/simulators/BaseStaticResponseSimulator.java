package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import jakarta.annotation.Nullable;

import java.util.List;

public abstract class BaseStaticResponseSimulator extends BaseSOAPSimulator {

    private final List<String> keys;

    protected BaseStaticResponseSimulator(ResourceFinder finder, List<String> keys) {
        super(finder);
        this.keys = keys;
    }

    protected BaseStaticResponseSimulator(List<String> keys) {
        super(null);
        this.keys = keys;
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaMockException {
        var serviceName = request.getValue("//Verzoek/Context/Naam");
        var serviceVersion = request.getValue("//Verzoek/Context/Versie");
        var values = keys.stream().map(request::getValue).toList();

        validatePathElement(serviceName);
        validatePathElement(serviceVersion);
        values.forEach(this::validatePathElement);

        var responseBody = loadResource(serviceName, serviceVersion, values);

        if (responseBody != null) {
            patchResponse(request, responseBody);

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaMockException("No mock data found for request to %s %s".formatted(serviceName, serviceVersion));
        }
    }

    @Nullable
    protected abstract MagdaDocument loadResource(String serviceName, String serviceVersion, List<String> values);
}
