package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;

import java.util.Optional;
import java.util.UUID;

/**
 * A service for interfacing with MAGDA's "LED.GeefBewijs" services for retrieving information on a citizen's diploma proofs.
 */
public interface GeefBewijzenService {

    /**
     * Retrieves diploma proof information, if any, from a GeefBewijsRequest.
     *
     * @see Bewijzen
     * @see GeefBewijsRequest
     */
    Optional<Bewijzen> geefBewijzen(GeefBewijsRequest request) throws MagdaClientException;

    /**
     * Retrieves diploma proof information, if any, from a GeefBewijsRequest, with a provided request ID.
     *
     * @see Bewijzen
     * @see GeefBewijsRequest
     */
    Optional<Bewijzen> geefBewijzen(GeefBewijsRequest request, UUID requestId) throws MagdaClientException;
}
