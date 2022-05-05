package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;

public interface MagdaEndpoints {
    String magdaUrl(MagdaServiceIdentificatie aanvraag);

    void addMapping(String dienstNaam, String versie, String prod);

    void addMapping(String dienstNaam, String versie, String prod, String tni);
}
