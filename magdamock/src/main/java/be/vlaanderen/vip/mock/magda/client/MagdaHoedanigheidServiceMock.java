package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;

public class MagdaHoedanigheidServiceMock implements MagdaHoedanigheidService {
    private final MagdaRegistrationInfo mockedMagdaRegistrationInfo;

    public MagdaHoedanigheidServiceMock(MagdaRegistrationInfo mockedMagdaRegistrationInfo) {
        this.mockedMagdaRegistrationInfo = mockedMagdaRegistrationInfo;
    }

    public MagdaRegistrationInfo getDomeinService() {
        return mockedMagdaRegistrationInfo;
    }

    public MagdaRegistrationInfo getDomeinService(String name) {
        return mockedMagdaRegistrationInfo;
    }
}
