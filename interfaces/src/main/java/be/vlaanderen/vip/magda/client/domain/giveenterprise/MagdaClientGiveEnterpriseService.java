package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefOndernemingRequest;
import jakarta.annotation.Nullable;

public class MagdaClientGiveEnterpriseService implements GiveEnterpriseService {

    private final MagdaClient service;
    private final MagdaResponseEnterpriseAdapter adapter;

    public MagdaClientGiveEnterpriseService(
            MagdaClient service,
            MagdaResponseEnterpriseAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    @Nullable
    public Enterprise getEnterprise(GeefOndernemingRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
