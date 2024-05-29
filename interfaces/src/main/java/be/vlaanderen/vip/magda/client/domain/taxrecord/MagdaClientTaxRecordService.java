package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;

public class MagdaClientTaxRecordService implements TaxRecordService {

    private final MagdaClient service;

    public MagdaClientTaxRecordService(
            MagdaClient service) {
        this.service = service;
    }

    @Override
    public TaxRecordList getTaxRecordList(GeefAanslagbiljetPersonenbelastingRequest request) throws MagdaClientException {
        return new MagdaResponseTaxRecordList(service.send(request));
    }
}