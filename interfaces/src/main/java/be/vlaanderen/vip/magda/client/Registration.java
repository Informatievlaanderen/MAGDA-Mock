package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

public interface Registration {

    MagdaRegistrationInfo resolve(MagdaHoedanigheidService magdaHoedanigheidService);
}