package be.vlaanderen.vip.mock.magda.client.simulators;

import java.util.Arrays;
import java.util.List;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.util.INSZ;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

public class RandomPasfotoSimulator extends SOAPSimulator {
    private final String type;
    private final List<String> keys;

    public RandomPasfotoSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder);
        this.type = type;
        this.keys = keys;
    }

    public RandomPasfotoSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        var params = new MagdaRequest(request, keys);

        var dienst = params.getServiceNaam();
        var versie = params.getServiceVersie();

        var responseBody = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, params.getKeys().get(0)));
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type,randomPasFotoresourcePath(dienst, versie, params.getKeys().get(0)));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, "succes"));
        }

        if (responseBody != null) {
            patchResponse(params, responseBody);

            // Patch response gebaseerd op request input
            responseBody.setValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ", params.getKeys().get(0));

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaSendFailed("Geen mock data gevonden voor request naar " + dienst + " " + versie);
        }
    }

    private String randomPasFotoresourcePath(String dienst, String versie, String insz) {
        String path = dienst + "/" + versie;

        String geboorteDatum = insz.substring(0, 6);
        int dateOfBirth = Integer.parseInt(geboorteDatum);

        if (INSZ.isMannelijk(insz)) {
            path = path + "/mannen/" + (dateOfBirth % 6) + ".xml";
        } else {
            path = path + "/vrouwen/" + (dateOfBirth % 4) + ".xml";
        }

        return path;
    }

    private String exactPasFotoresourcePath(String dienst, String versie, String insz) {
        String path = dienst + "/" + versie + "/" + insz + ".xml";

        return path;
    }


}
