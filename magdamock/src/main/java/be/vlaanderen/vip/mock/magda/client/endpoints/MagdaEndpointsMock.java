package be.vlaanderen.vip.mock.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;

public class MagdaEndpointsMock implements MagdaEndpoints {
    public String magdaUrl(MagdaServiceIdentificatie aanvraag) {
        return "local mock";
    }

    public void addMapping(String dienstNaam, String versie, String prod) {

    }

    public void addMapping(String dienstNaam, String versie, String prod, String tni) {

    }
}
