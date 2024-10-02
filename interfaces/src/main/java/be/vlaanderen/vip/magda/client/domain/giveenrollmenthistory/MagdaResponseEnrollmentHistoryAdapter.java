package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

import java.util.Optional;

public interface MagdaResponseEnrollmentHistoryAdapter {

    Optional<EnrollmentHistory> adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}