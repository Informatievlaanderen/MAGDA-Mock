package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;

/**
 * A service for interfacing with MAGDA's "GeefAanslagbiljetPersonenbelasting" services for retrieving information on the tax records of citizens.
 */
public interface TaxRecordService {

    /**
     * Retrieves tax record information from a GeefAanslagbiljetPersonenbelasting request.
     *
     * @see TaxRecordList
     * @see GeefAanslagbiljetPersonenbelastingRequest
     */
    TaxRecordList getTaxRecordList(GeefAanslagbiljetPersonenbelastingRequest request) throws MagdaClientException;
}
