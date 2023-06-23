package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * MAGDA has sent a response.
 * Adds the following fields to the {@link LoggedRequest}:
 * <ul>
 * <li>duration until the response was received, in nanoseconds</li>
 * </ul>
 */
@Getter
public class SucceededLoggedRequest extends LoggedRequest {
    private final Duration duration;

    public SucceededLoggedRequest(List<String> inszs,
                                  UUID transactionID,
                                  UUID localTransactionID,
                                  Duration duration,
                                  String serviceName,
                                  String serviceVersion,
                                  MagdaRegistrationInfo registrationInfo) {
        super(inszs, transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
        this.duration = duration;
    }
}
