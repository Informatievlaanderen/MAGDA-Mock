package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefFunctiesByPersonRequest;

public class MagdaClientGiveEnterpriseFunctionsService implements GiveEnterpriseFunctionsService {

    private final MagdaClient service;
    private final MagdaResponseEnterpriseFunctionsAdapter adapter;

    public MagdaClientGiveEnterpriseFunctionsService(
            MagdaClient service,
            MagdaResponseEnterpriseFunctionsAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public EnterpriseFunctions getEnterpriseFunctions(GeefFunctiesByPersonRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
