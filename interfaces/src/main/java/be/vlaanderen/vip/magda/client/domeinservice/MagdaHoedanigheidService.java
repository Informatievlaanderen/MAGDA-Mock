package be.vlaanderen.vip.magda.client.domeinservice;

/**
 * An interface to provide {@link MagdaRegistrationInfo} by resolving a registration code in some way.
 */
public interface MagdaHoedanigheidService {

    MagdaRegistrationInfo getDomeinService(String registration);
    
}
