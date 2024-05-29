package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefWerkrelatiesRequest;

/**
 * A service for interfacing with MAGDA's "GeefWerkrelaties" services for retrieving information on a person's work relations (employees, etc.).
 */
public interface GiveWorkRelationsService {

    /**
     * Retrieves enterprise information from a GeefOnderneming request.
     *
     * @see WorkRelations
     * @see GeefWerkrelatiesRequest
     */
    WorkRelations getWorkRelations(GeefWerkrelatiesRequest request) throws MagdaClientException;
}