package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;

import java.util.List;

/**
 * A service for interfacing with MAGDA's "Mobility Registration" services for retrieving information on a vehicle registrations.
 */
public interface MobilityRegistrationService {
    /**
     * Retrieves the registrations given a registration request.
     *
     * @see Registration
     * @see MobilityRegistrationRequest
     */
    List<Registration> getRegistrations(MobilityRegistrationRequest request) throws MagdaClientException;
}
