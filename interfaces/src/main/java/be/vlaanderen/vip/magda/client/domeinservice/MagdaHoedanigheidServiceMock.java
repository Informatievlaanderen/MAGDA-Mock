package be.vlaanderen.vip.magda.client.domeinservice;

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
