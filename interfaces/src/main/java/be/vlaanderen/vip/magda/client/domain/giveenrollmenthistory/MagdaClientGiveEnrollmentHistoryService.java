package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;

public class MagdaClientGiveEnrollmentHistoryService implements GiveEnrollmentHistoryService {

    private final MagdaClient service;
    private final MagdaResponseEnrollmentHistoryAdapter adapter;

    public MagdaClientGiveEnrollmentHistoryService(
            MagdaClient service,
            MagdaResponseEnrollmentHistoryAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    public MagdaClientGiveEnrollmentHistoryService(
            MagdaClient service) {
        this(service, MagdaResponseEnrollmentHistoryAdapterJaxbImpl.getInstance());
    }

    @Override
    public EnrollmentHistory getEnrollmentHistory(GeefHistoriekInschrijvingRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
