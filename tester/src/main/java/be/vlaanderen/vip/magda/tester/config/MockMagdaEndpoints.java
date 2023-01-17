package be.vlaanderen.vip.magda.tester.config;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockMagdaEndpoints implements MagdaEndpoints {
    private final String url;

    @Override
    public String magdaUrl(MagdaServiceIdentificatie aanvraag) {
        return url;
    }

    @Override
    public void addMapping(String dienstNaam, String versie, String prod) {

    }

    @Override
    public void addMapping(String dienstNaam, String versie, String prod, String tni) {

    }
}
