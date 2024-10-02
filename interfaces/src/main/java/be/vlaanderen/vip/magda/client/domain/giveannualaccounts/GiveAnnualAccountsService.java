package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefJaarrekeningenRequest;

/**
 * A service for interfacing with MAGDA's "GeefJaarrekeningen" services for retrieving information on the annual accounts of enterprises.
 */
public interface GiveAnnualAccountsService {

    /**
     * Retrieves annual account information from a GeefJaarrekeningen request.
     *
     * @see AnnualAccounts
     * @see GeefJaarrekeningenRequest
     */
    AnnualAccounts getAnnualAccounts(GeefJaarrekeningenRequest request) throws MagdaClientException;
}