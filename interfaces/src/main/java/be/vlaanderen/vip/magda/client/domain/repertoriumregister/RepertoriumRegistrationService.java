package be.vlaanderen.vip.magda.client.domain.repertoriumregister;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.dto.RegisteredINSZ;

import java.util.function.Supplier;

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
    RegisteredINSZ register(Supplier<RegistreerInschrijvingRequest> request) throws MagdaClientException;
}
