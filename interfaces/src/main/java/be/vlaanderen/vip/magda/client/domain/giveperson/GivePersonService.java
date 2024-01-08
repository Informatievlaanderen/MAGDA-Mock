package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;

/**
 * A service for interfacing with MAGDA's "GeefPersoon" services for retrieving personal information on citizens.
 */
public interface GivePersonService {

    /**
     * Retrieves personal information from a GeefPersoon request.
     *
     * @see Person
     * @see GeefPersoonRequest
     */
    Person getPerson(GeefPersoonRequest request) throws MagdaClientException;
    
}
