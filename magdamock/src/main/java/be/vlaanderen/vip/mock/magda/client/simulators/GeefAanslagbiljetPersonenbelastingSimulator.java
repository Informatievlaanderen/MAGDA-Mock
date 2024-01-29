package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.util.Arrays;
import java.util.List;

public class GeefAanslagbiljetPersonenbelastingSimulator extends StaticResponseSimulator {
    boolean patchInkomensjaar;

    public GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinder finder, String type, boolean patchInkomensjaar, List<String> keys) {
        super(finder, type, keys);
        this.patchInkomensjaar = patchInkomensjaar;
    }

    public GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinder finder, String type, boolean patchInkomensjaar, String... keys) {
        this(finder, type, patchInkomensjaar, Arrays.asList(keys));
    }

    protected void patchResponse(MagdaDocument request, MagdaDocument response) {
        super.patchResponse(request, response);

        if(patchInkomensjaar) {
            response.setValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar",
                    request.getValue("//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar"));
        }
    }
}
