package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import java.util.UUID;

/**
 * Not authorized to perform this request
 * <p>
 * Adds the following fields to {@link LoggedRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 */
public class UnauthorizedLoggedRequest extends LoggedRequest {
    public UnauthorizedLoggedRequest(String requestingPartyInsz, UUID transactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(requestingPartyInsz, null, transactionID, null, serviceName, serviceVersion, registrationInfo);
    }

    public UnauthorizedLoggedRequest(String requestingPartyInsz, UUID transactionID, UUID localTransactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(requestingPartyInsz, null, transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
    }
}
