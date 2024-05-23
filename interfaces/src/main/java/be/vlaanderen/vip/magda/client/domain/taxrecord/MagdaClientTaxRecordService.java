package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.correlation.NullCorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;

import java.util.UUID;

public class MagdaClientTaxRecordService implements TaxRecordService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;

    /**
     * @deprecated remove the correlationHeaderProvider parameters, and for all the relevant requests, replace all uses of a CorrelationHeaderProvider with request.setCorrelationId(correlationId)`.
     */
    public MagdaClientTaxRecordService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    public MagdaClientTaxRecordService(
            MagdaClient service) {
        this.service = service;
        this.correlationHeaderProvider = NullCorrelationHeaderProvider.getInstance();
    }

    @Override
    public TaxRecordList getTaxRecordList(GeefAanslagbiljetPersonenbelastingRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId)));

        return new MagdaResponseTaxRecordList(service.send(request));
    }
}