package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefOndernemingRequest;
import jakarta.annotation.Nullable;

/**
 * A service for interfacing with MAGDA's "GeefOnderneming" services for retrieving information on enterprises.
 */
public interface GiveEnterpriseService {

    /**
     * Retrieves enterprise information from a GeefOnderneming request.
     *
     * @return the enterprise information, or null when it's absent in the response.
     * @see Enterprise
     * @see GeefOndernemingRequest
     */
    @Nullable
    Enterprise getEnterprise(GeefOndernemingRequest request) throws MagdaClientException;
}