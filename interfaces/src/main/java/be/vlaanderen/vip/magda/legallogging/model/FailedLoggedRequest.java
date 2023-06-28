package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * MAGDA has sent a response, but it contains a list of {@link UitzonderingEntry}.
 * Adds the following fields to {@link LoggedRequest}:
 * <ul>
 * <li>duration until the response was received, in nanoseconds</li>
 * <li>list of {@link UitzonderingEntry} that MAGDA included in the response</li>
 * </ul>
 */
@Getter
public class FailedLoggedRequest extends LoggedRequest {
    private final Duration duration;
    private final List<UitzonderingEntry> uitzonderingEntries;

    public FailedLoggedRequest(UUID transactionID,
                               UUID localTransactionID,
                               Duration duration,
                               List<UitzonderingEntry> uitzonderingEntries,
                               String serviceName,
                               String serviceVersion,
                               MagdaRegistrationInfo registrationInfo) {
        super(List.of(), transactionID, localTransactionID, serviceName, serviceVersion, registrationInfo);
        this.uitzonderingEntries = uitzonderingEntries;
        this.duration = duration;
    }
}
