package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;

/**
 * A service for interfacing with MAGDA's "Onderwijs.GeefHistoriekInschrijving" services for retrieving information on a person's education enrollment history.
 */
public interface GiveEnrollmentHistoryService {

    /**
     * Retrieves education enrollment history information from a GeefHistoriekInschrijvingRequest request.
     *
     * @see EnrollmentHistory
     * @see GeefHistoriekInschrijvingRequest
     */
    EnrollmentHistory getEnrollmentHistory(GeefHistoriekInschrijvingRequest request) throws MagdaClientException;
}
