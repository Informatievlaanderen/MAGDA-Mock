package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import java.util.Collections;
import java.util.UUID;

/**
 * MAGDA sent no response, communication error or timeout.
 * <p>
 * Adds the following fields to {@link LoggedRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 */
public class UnansweredLoggedRequest extends LoggedRequest {
    public UnansweredLoggedRequest(String insz, String aboutWhom, UUID transactionID, UUID localTransactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(insz, Collections.singletonList(aboutWhom), transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
    }
}
