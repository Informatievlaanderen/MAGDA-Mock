package be.vlaanderen.vip.magda.client.domain.givearzacareer;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefLoopbaanARZARequest;

public class MagdaClientGiveARZACareerService implements GiveARZACareerService {

    private final MagdaClient service;
    private final MagdaResponseARZACareerAdapter adapter;

    public MagdaClientGiveARZACareerService(
            MagdaClient service,
            MagdaResponseARZACareerAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public ARZACareer getARZACareer(GeefLoopbaanARZARequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
