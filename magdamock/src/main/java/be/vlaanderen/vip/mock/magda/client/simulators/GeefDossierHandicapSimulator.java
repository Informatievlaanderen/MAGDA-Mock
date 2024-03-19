package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.util.Arrays;
import java.util.List;

public class GeefDossierHandicapSimulator extends StaticResponseSimulator {
    public GeefDossierHandicapSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder, type, keys);
    }

    public GeefDossierHandicapSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    protected void patchResponse(MagdaDocument request, MagdaDocument response) {
        super.patchResponse(request, response);

        //GeefDossierHandicapByDateRequest
        if(response.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse") != null) {
            response.setValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse/ssin",
                    request.getValue("//Vragen/Vraag/Inhoud/Criteria/ConsultFilesByDateCriteria/ssin"));
        }

        //GeefDossierHandicapByPeriodRequest
        if(response.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByPeriodResponse") != null) {
            response.setValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByPeriodCriteria/ssin",
                    request.getValue("//Vragen/Vraag/Inhoud/Criteria/ConsultFilesByPeriodCriteria/ssin"));
        }
    }
}
