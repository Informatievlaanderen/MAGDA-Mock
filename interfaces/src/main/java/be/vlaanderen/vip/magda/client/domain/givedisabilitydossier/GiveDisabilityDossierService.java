package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDossierHandicapByDateRequest;

/**
 * A service for interfacing with MAGDA's "GeefDossierHandicap" services for retrieving information on a person's handicap information.
 */
public interface GiveDisabilityDossierService {
    /**
     * Retrieves disability dossier from a GeefDossierHandicapByDateRequest request
     *
     * @see DisabilityDossier
     * @see GeefDossierHandicapByDateRequest
     */
    DisabilityDossier getDisabilityDossier(GeefDossierHandicapByDateRequest request) throws MagdaClientException;
}
