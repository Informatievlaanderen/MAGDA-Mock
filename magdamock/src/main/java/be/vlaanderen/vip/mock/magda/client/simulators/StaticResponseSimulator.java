package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticResponseSimulator extends BaseSOAPSimulator {
    private final String type;
    private final List<String> keys;

    public StaticResponseSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder);
        this.type = type;
        this.keys = keys;
    }

    public StaticResponseSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
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
        if (responseBody == null) {
            responseBody = loadResource(serviceName, serviceVersion, replaceLastKey(values, "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadResource(serviceName, serviceVersion, replaceLastKey(values, "success"));
        }

        if (responseBody != null) {
            patchResponse(request, responseBody);

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaMockException("No mock data found for request to %s %s".formatted(serviceName, serviceVersion));
        }
    }

    private List<String> replaceLastKey(List<String> original, String replaceFinal) {
        var result = new ArrayList<String>();
        for (var i = 0; i < (original.size() - 1); i++) {
            result.add(original.get(i));
        }
        result.add(replaceFinal);
        return result;
    }

    private MagdaDocument loadResource(String serviceName, String serviceVersion, List<String> keys) {
        var dirs = new ArrayList<String>();
        dirs.add(serviceName);
        dirs.add(serviceVersion);
        if (keys.size() > 1) {
            for (var i = 0; i < keys.size() - 1; i++) {
                if (keys.get(i) != null) {
                    dirs.add(keys.get(i));
                }
            }
        }

        var fileName = keys.get(keys.size() - 1) + ".xml";

        return loadSimulatorResourceFromHierarchy(type, dirs, fileName);
    }

    private MagdaDocument loadSimulatorResourceFromHierarchy(String type, List<String> dirs, String fileName) {
        while(!dirs.isEmpty()) {
            var testResource = String.join("/", dirs) + "/" + fileName;
            var simulatorResource = loadSimulatorResource(type, testResource);

            if(simulatorResource == null) {
                dirs.remove(dirs.size() - 1);
            } else {
                return simulatorResource;
            }
        }

        return loadSimulatorResource(type, fileName);
    }
}
