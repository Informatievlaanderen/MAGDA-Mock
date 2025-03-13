package be.vlaanderen.vip.magda.client.domain.registreerbewijs;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.RegistreerBewijsRequest;

/**
 * A service for interfacing with MAGDA's "LED.RegistreerBewijs" services for registering proofs in LED.
 */
public interface RegistreerBewijsService {

    /**
     * Registers a proof in LED from a RegistreerBewijsRequest.
     *
     * @see RegistreerBewijsRequest
     */
    void registreerBewijs(RegistreerBewijsRequest request) throws MagdaClientException;
}
