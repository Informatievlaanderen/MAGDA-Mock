package be.vlaanderen.vip.magda.client.domain.repertoriumregister;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.dto.RegisteredINSZ;

/**
 * A service for interfacing with MAGDA's "RegistreerInschrijving" services for registering INSZ numbers in the Repertorium.
 */
public interface RepertoriumRegistrationService {
    /**
     * Registers an INSZ number in the Repertorium from a RegistreerInschrijving request.
     *
     * @see RegisteredINSZ
     * @see RegistreerInschrijvingRequest
     */
    RegisteredINSZ register(RegistreerInschrijvingRequest request) throws MagdaClientException;
}
