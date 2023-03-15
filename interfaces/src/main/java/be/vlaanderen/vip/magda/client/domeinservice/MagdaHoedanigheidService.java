package be.vlaanderen.vip.magda.client.domeinservice;

public interface MagdaHoedanigheidService {
    MagdaRegistrationInfo getDomeinService();

    MagdaRegistrationInfo getDomeinService(String name);
}
