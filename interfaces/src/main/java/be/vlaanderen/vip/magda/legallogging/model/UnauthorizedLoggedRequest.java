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
    public UnauthorizedLoggedRequest(String insz, UUID transactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(insz, null, transactionID, null, serviceName, serviceVersion, registrationInfo);
    }

    public UnauthorizedLoggedRequest(String insz, UUID transactionID, UUID localTransactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(insz, null, transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
    }
}
