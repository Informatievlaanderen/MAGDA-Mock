package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class StaticResponseSimulator extends SOAPSimulator {
    private final String type;
    private final List<String> keys;

    public StaticResponseSimulator(String type, List<String> keys) {
        this.type = type;
        this.keys = keys;
    }

    public StaticResponseSimulator(String type, String... keys) {
        this.type = type;
        this.keys = Arrays.asList(keys);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        var params = new MagdaRequest(request, keys);

        var dienst = params.getServiceNaam();
        var versie = params.getServiceVersie();

        var responseBody = loadResource(dienst, versie, params.getKeys());
        if (responseBody == null) {
            responseBody = loadResource(dienst, versie, replaceLastKey(params.getKeys(), "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadResource(dienst, versie, replaceLastKey(params.getKeys(), "success"));
        }

        if (responseBody != null) {
            PatchResponse(params, responseBody);

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaSendFailed("Geen mock data gevonden voor request naar " + dienst + " " + versie);
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

    private MagdaDocument loadResource(String dienst, String versie, List<String> keys) {
        var dirs = new ArrayList<String>();
        dirs.add(dienst);
        dirs.add(versie);
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
