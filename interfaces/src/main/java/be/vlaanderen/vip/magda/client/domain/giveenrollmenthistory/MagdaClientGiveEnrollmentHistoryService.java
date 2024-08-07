package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;

import java.util.Optional;
import java.util.UUID;

public class MagdaClientGiveEnrollmentHistoryService implements GiveEnrollmentHistoryService {

    private final MagdaClient client;
    private final MagdaResponseEnrollmentHistoryAdapter adapter;

    public MagdaClientGiveEnrollmentHistoryService(
            MagdaClient client,
            MagdaResponseEnrollmentHistoryAdapter adapter) {
        this.client = client;
        this.adapter = adapter;
    }

    public MagdaClientGiveEnrollmentHistoryService(
            MagdaClient service) {
        this(service, MagdaResponseEnrollmentHistoryAdapterJaxbImpl.getInstance());
    }

    @Override
    public Optional<EnrollmentHistory> getEnrollmentHistory(GeefHistoriekInschrijvingRequest request) throws MagdaClientException {
        var responseWrapper = client.send(request);

        validateResponse(responseWrapper.getResponse(), request);

        return adapter.adapt(responseWrapper);
    }

    @Override
    public Optional<EnrollmentHistory> getEnrollmentHistory(GeefHistoriekInschrijvingRequest request, UUID requestId) throws MagdaClientException {
        var responseWrapper = client.send(request, requestId);

        validateResponse(responseWrapper.getResponse(), request);

        return adapter.adapt(responseWrapper);
    }

    private void validateResponse(MagdaResponse response, GeefHistoriekInschrijvingRequest request) throws MagdaClientException {
        if(response.getResponseUitzonderingEntries().stream().anyMatch(x ->
                        x.getUitzonderingType().equals(UitzonderingType.FOUT) &&
                        !"30101".equals(x.getIdentification()))) {
            throw new MagdaClientException("Level 3 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getResponseUitzonderingEntries(), request.getCorrelationId(), response.getRequestId()));
        }
    }
}
