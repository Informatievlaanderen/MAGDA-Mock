package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;

public class MagdaHoedanigheidServiceMock implements MagdaHoedanigheidService {
    private final MagdaRegistrationInfo mockedMagdaRegistrationInfo;

    public MagdaHoedanigheidServiceMock(MagdaRegistrationInfo mockedMagdaRegistrationInfo) {
        this.mockedMagdaRegistrationInfo = mockedMagdaRegistrationInfo;
    }

    @Override
    public MagdaRegistrationInfo getDomeinService(String registration) {
        return mockedMagdaRegistrationInfo;
    }
}
