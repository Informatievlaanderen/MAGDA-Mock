package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MagdaEndpointsImpl implements MagdaEndpoints {
    private final Map<MagdaServiceIdentificatie, MagdaEndpoint> endpoints;
    private final MagdaConfigDto config;

    // Voeg een item toe telkens een nieuwe dienst gebruikt wordt
    public MagdaEndpointsImpl(MagdaConfigDto config) {
        this.config = config;
        this.endpoints = new HashMap<>();
    }

    public String magdaUrl(MagdaServiceIdentificatie aanvraag) {
        MagdaEndpoint endpoint = bepaalMagdaPath(aanvraag);
        final String url = config.getEnvironment();

        if ("TEST".equalsIgnoreCase(url)) {
            return endpoint.getTni().toString();
        } else if ("PROD".equalsIgnoreCase(url)) {
            return endpoint.getProd().toString();
        } else {
            return (url + endpoint.getPath());
        }
    }

    private MagdaEndpoint bepaalMagdaPath(MagdaServiceIdentificatie dienst) {
        final MagdaEndpoint magdaEndpoint = endpoints.get(dienst);
        if (magdaEndpoint == null) {
            throw new IllegalArgumentException("Geen MagdaEndpoint geconfigureerd voor dienst '" + dienst + "'. Voeg deze toe in MagdaEndpoints");
        }
        return magdaEndpoint;
    }

    public void addMapping(String dienstNaam, String versie, String prod) {
        addMapping(dienstNaam, versie, prod, prod.replace(".vlaanderen.be", "-aip.vlaanderen.be"));
    }

    public void addMapping(String dienstNaam, String versie, String prod, String tni) {
        try {
            URL tniUrl = new URL(tni);
            URL prodUrl = new URL(prod);
            endpoints.put(new MagdaServiceIdentificatie(dienstNaam, versie), new MagdaEndpoint(tniUrl, prodUrl));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Fout in configuratie Magda URLs", e);
        }
    }
}
