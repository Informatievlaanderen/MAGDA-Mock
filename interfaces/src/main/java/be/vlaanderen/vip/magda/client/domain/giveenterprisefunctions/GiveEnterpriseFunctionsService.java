package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefFunctiesByPersonRequest;

/**
 * A service for interfacing with MAGDA's "GeefFunctiesByPerson" services for retrieving information on a person's job positions.
 */
public interface GiveEnterpriseFunctionsService {

    /**
     * Retrieve information on a person's job positions.
     *
     * @see EnterpriseFunctions
     * @see GeefFunctiesByPersonRequest
     */
    EnterpriseFunctions getEnterpriseFunctions(GeefFunctiesByPersonRequest request) throws MagdaClientException;
}