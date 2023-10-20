package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.util.Arrays;
import java.util.List;

public class GeefAanslagbiljetPersonenbelastingSimulator extends StaticResponseSimulator {

    public GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder, type, keys);
    }

    public GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    protected void patchResponse(MagdaDocument request, MagdaDocument response) {
        super.patchResponse(request, response);

        response.setValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar",
                request.getValue("//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar"));
    }
}