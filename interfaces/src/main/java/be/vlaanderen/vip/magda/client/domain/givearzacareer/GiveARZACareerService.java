package be.vlaanderen.vip.magda.client.domain.givearzacareer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefLoopbaanARZARequest;

/**
 * A service for interfacing with MAGDA's "GeefLoopbaanARZA" services for retrieving information on a person's career in the ARZA register.
 */
public interface GiveARZACareerService {

    /**
     * Retrieves career information from a GeefLoopbaanARZA request.
     *
     * @see ARZACareer
     * @see GeefLoopbaanARZARequest
     */
    ARZACareer getARZACareer(GeefLoopbaanARZARequest request) throws MagdaClientException;
}