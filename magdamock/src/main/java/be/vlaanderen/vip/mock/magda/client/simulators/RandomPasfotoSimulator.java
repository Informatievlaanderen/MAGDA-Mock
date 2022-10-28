package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.util.INSZ;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RandomPasfotoSimulator extends SOAPSimulator {
    private final String type;
    private final List<String> keys;

    public RandomPasfotoSimulator(String type, List<String> keys) {
        this.type = type;
        this.keys = keys;
    }

    public RandomPasfotoSimulator(String type, String... keys) {
        this.type = type;
        this.keys = Arrays.asList(keys);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) {
        var params = new MagdaRequest(request, keys);

        var dienst = params.getServiceNaam();
        var versie = params.getServiceVersie();

        var response = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, params.getKeys().get(0)));
        if (response == null) {
            response = loadSimulatorResource(type,randomPasFotoresourcePath(dienst, versie, params.getKeys().get(0)));
        }
        if (response == null) {
            response = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, "notfound"));
        }
        if (response == null) {
            response = loadSimulatorResource(type,exactPasFotoresourcePath(dienst, versie, "succes"));
        }

        if (response != null) {
            PatchResponse(params, response);

            // Patch response gebaseerd op request input
            response.setValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ", params.getKeys().get(0));

            return response;
        } else {
            log.warn("Geen mock data gevonden voor request naar {} {}", dienst, versie);
            return null;
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
