package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathBasedStaticResponseSimulator extends BaseStaticResponseSimulator {

    private final String type;

    public PathBasedStaticResponseSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder, keys);
        this.type = type;
    }

    public PathBasedStaticResponseSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    @Override
    @Nullable
    protected MagdaDocument loadResource(String serviceName, String serviceVersion, List<String> values) {
        var responseBody = loadPathBasedResource(serviceName, serviceVersion, values);
        if (responseBody == null) {
            responseBody = loadPathBasedResource(serviceName, serviceVersion, replaceLastKey(values, "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadPathBasedResource(serviceName, serviceVersion, replaceLastKey(values, "success"));
        }

        return responseBody;
    }

    private List<String> replaceLastKey(List<String> original, String replaceFinal) {
        var result = new ArrayList<String>();
        for (var i = 0; i < (original.size() - 1); i++) {
            result.add(original.get(i));
        }
        result.add(replaceFinal);
        return result;
    }

    private MagdaDocument loadPathBasedResource(String serviceName, String serviceVersion, List<String> keys) {
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
