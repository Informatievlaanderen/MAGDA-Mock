package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Base class for all request logs.
 * <p>
 * Contains:
 * <ul>
 * <li>INSZ number of the person that performed the request</li>
 * <li>Unique transaction ID of the request</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class LoggedRequest {
    private final List<SubjectIdentificationNumber> subjects;
    private final UUID transactionID;
    private final UUID localTransactionID;
    private final String serviceName;
    private final String serviceVersion;
    private final MagdaRegistrationInfo registrationInfo;

    public String getSubjectsInLogFormat() {
        if(getSubjects().isEmpty()) {
            return "(none)";
        } else {
            return String.join(", ", getSubjects().stream().map(SubjectIdentificationNumber::getValueInLogFormat).toList());
        }
    }
}
