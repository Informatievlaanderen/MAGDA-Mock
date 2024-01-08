package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;

/**
 * A service for interfacing with MAGDA's "GeefSociaalStatuut" services for retrieving information on the social statute of citizens.
 */
public interface SocialStatuteService {

    /**
     * Retrieves social statute information from a GeefSociaalStatuut request.
     *
     * @see SocialStatute
     * @see GeefSociaalStatuutRequest
     */
    SocialStatute getSocialStatute(GeefSociaalStatuutRequest request) throws MagdaClientException;
}
