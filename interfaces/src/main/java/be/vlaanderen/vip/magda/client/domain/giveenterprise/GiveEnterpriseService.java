package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefOndernemingRequest;

/**
 * A service for interfacing with MAGDA's "GeefOnderneming" services for retrieving information on enterprises.
 */
public interface GiveEnterpriseService {

    /**
     * Retrieves enterprise information from a GeefOnderneming request.
     *
     * @see Enterprise
     * @see GeefOndernemingRequest
     */
    Enterprise getEnterprise(GeefOndernemingRequest request) throws MagdaClientException;
}