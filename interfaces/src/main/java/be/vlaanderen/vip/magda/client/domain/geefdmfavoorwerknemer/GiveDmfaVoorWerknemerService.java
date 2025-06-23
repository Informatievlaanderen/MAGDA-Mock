package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDmfaVoorWerknemerRequest;
import be.vlaanderen.vip.magda.client.domain.dto.WithMagdaResponse;

/**
 * A service for interfacing with MAGDA's "GeefDmfaVoorWerknemer" services for retrieving information on a person's DmfA certificates (wage and work time information for each quarter).
 */
public interface GiveDmfaVoorWerknemerService {

    /**
     * Retrieves dfma certificates from a GeefDmfaVoorWerknemer request.
     *
     * @see DmfaAttest
     * @see GeefDmfaVoorWerknemerRequest
     */
    DmfaAttest getDmfaAttests(GeefDmfaVoorWerknemerRequest request) throws MagdaClientException;

    /**
     * Retrieves dfma certificates from a GeefDmfaVoorWerknemer request.
     * Includes the source MagdaResponse from which the DmfaAttest is mapped.
     *
     * @see WithMagdaResponse
     * @see be.vlaanderen.vip.magda.client.MagdaResponse
     * @see DmfaAttest
     * @see GeefDmfaVoorWerknemerRequest
     */
    WithMagdaResponse<DmfaAttest> getDmfaAttestsWithSourceResponse(GeefDmfaVoorWerknemerRequest request) throws MagdaClientException;
}