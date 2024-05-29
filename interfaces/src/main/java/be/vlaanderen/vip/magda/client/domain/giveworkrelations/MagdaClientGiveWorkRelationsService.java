package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefWerkrelatiesRequest;

public class MagdaClientGiveWorkRelationsService implements GiveWorkRelationsService {

    private final MagdaClient service;
    private final MagdaResponseWorkRelationsAdapter adapter;

    public MagdaClientGiveWorkRelationsService(
            MagdaClient service,
            MagdaResponseWorkRelationsAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public WorkRelations getWorkRelations(GeefWerkrelatiesRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
