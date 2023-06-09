package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
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

    public MagdaLoggedRequest(SubjectIdentificationNumber subject, UUID transactionID, UUID localTransactionID, String serviceName, String serviceVersion, MagdaRegistrationInfo registrationInfo) {
        super(Collections.singletonList(subject), transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
    }
}
