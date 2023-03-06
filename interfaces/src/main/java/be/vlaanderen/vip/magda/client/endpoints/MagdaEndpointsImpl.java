package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MagdaEndpointsImpl implements MagdaEndpoints { // XXX maybe get rid of this class entirely?

    private final Map<MagdaServiceIdentificatie, MagdaEndpoint> endpoints = new HashMap<>();;

    public URI magdaUri(MagdaServiceIdentificatie aanvraag) {
        return bepaalMagdaPath(aanvraag).uri();
    }

    private MagdaEndpoint bepaalMagdaPath(MagdaServiceIdentificatie dienst) {
        final MagdaEndpoint magdaEndpoint = endpoints.get(dienst);
        if (magdaEndpoint == null) {
            throw new IllegalArgumentException("Geen MagdaEndpoint geconfigureerd voor dienst '" + dienst + "'. Voeg deze toe in MagdaEndpoints");
        }
        return magdaEndpoint;
    }

    public void addMapping(String dienstNaam, String versie, URI uri) {
        endpoints.put(new MagdaServiceIdentificatie(dienstNaam, versie), new MagdaEndpoint(uri));
    }
}
