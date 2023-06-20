package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ServiceMappedMagdaEndpoints implements MagdaEndpoints {

    private final Map<MagdaServiceIdentification, MagdaEndpoint> endpoints = new HashMap<>();

    public URI magdaUri(MagdaServiceIdentification aanvraag) {
        return determineMagdaPath(aanvraag).getUri();
    }

    private MagdaEndpoint determineMagdaPath(MagdaServiceIdentification dienst) {
        final var magdaEndpoint = endpoints.get(dienst);
        if(magdaEndpoint == null) {
            throw new IllegalArgumentException("No MagdaEndpoint configured for service '" + dienst + "'. Add them in MagdaEndpoints.");
        }
        return magdaEndpoint;
    }

    public void addMapping(String dienstNaam, String versie, MagdaEndpoint magdaEndpoint) {
        endpoints.put(new MagdaServiceIdentification(dienstNaam, versie), magdaEndpoint);
    }
}
