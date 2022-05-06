package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;

public class MagdaHoedanigheidServiceMock implements MagdaHoedanigheidService {
    private final MagdaHoedanigheid mockedMagdaHoedanigheid;

    public MagdaHoedanigheidServiceMock(MagdaHoedanigheid mockedMagdaHoedanigheid) {
        this.mockedMagdaHoedanigheid = mockedMagdaHoedanigheid;
    }

    public MagdaHoedanigheid getDomeinService() {
        return mockedMagdaHoedanigheid;
    }

    public MagdaHoedanigheid getDomeinService(String name) {
        return mockedMagdaHoedanigheid;
    }
}
