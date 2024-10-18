package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefJaarrekeningenRequest;

public class MagdaClientGiveAnnualAccountsService implements GiveAnnualAccountsService {

    private final MagdaClient service;
    private final MagdaResponseAnnualAccountsAdapter adapter;

    public MagdaClientGiveAnnualAccountsService(
            MagdaClient service,
            MagdaResponseAnnualAccountsAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public AnnualAccounts getAnnualAccounts(GeefJaarrekeningenRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
