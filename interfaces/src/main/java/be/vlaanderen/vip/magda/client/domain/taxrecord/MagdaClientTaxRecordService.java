package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;

import java.util.UUID;

public class MagdaClientTaxRecordService implements TaxRecordService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;

    public MagdaClientTaxRecordService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    @Override
    public TaxRecordList getTaxRecordList(GeefAanslagbiljetPersonenbelastingRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId)));

        return new MagdaResponseTaxRecordList(service.send(request));
    }
}