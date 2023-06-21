package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.util.Collections;
import java.util.UUID;

/**
 * A MAGDA request to be logged.
 * Adds the following fields to the {@link LoggedRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 */
@Getter
public class MagdaLoggedRequest extends LoggedRequest {

    public MagdaLoggedRequest(String insz, String aboutWhom, UUID transactionID, UUID localTransactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(insz, Collections.singletonList(aboutWhom), transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
    }
}
