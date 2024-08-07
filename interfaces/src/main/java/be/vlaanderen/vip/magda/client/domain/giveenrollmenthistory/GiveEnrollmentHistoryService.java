package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;

import java.util.Optional;
import java.util.UUID;

/**
 * A service for interfacing with MAGDA's "Onderwijs.GeefHistoriekInschrijving" services for retrieving information on a person's education enrollment history.
 */
public interface GiveEnrollmentHistoryService {

    /**
     * Retrieves education enrollment history information, if any, from a GeefHistoriekInschrijvingRequest request.
     *
     * @see EnrollmentHistory
     * @see GeefHistoriekInschrijvingRequest
     */
    Optional<EnrollmentHistory> getEnrollmentHistory(GeefHistoriekInschrijvingRequest request) throws MagdaClientException;

    /**
     * Retrieves education enrollment history information, if any, from a GeefHistoriekInschrijvingRequest request, with a provided request ID.
     *
     * @see EnrollmentHistory
     * @see GeefHistoriekInschrijvingRequest
     */
    Optional<EnrollmentHistory> getEnrollmentHistory(GeefHistoriekInschrijvingRequest request, UUID requestId) throws MagdaClientException;
}
