package be.vlaanderen.vip.magda.client.domain.givesocialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;

/**
 * A service for interfacing with MAGDA's "GeefSociaalStatuut" services for retrieving information on the social statute of citizens.
 */
public interface GiveSocialStatuteService {

    /**
     * Retrieves a list of social statutes from a GeefSociaalStatuutRequest request.
     *
     * @see SocialStatutes
     * @see GeefSociaalStatuutRequest
     */
    SocialStatutes getSocialStatutes(GeefSociaalStatuutRequest request) throws MagdaClientException;

    /**
     * Retrieves a list of social statutes from a GeefMultipleSociaalStatuutRequest request.
     *
     * @see SocialStatutes
     * @see GeefMultipleSociaalStatuutRequest
     */
    SocialStatutes getSocialStatutesByMultipleCriteria(GeefMultipleSociaalStatuutRequest request) throws MagdaClientException;
}
