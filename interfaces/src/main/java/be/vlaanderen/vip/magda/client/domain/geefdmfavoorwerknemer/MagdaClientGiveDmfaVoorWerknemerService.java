package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDmfaVoorWerknemerRequest;
import be.vlaanderen.vip.magda.client.domain.dto.WithMagdaResponse;

public class MagdaClientGiveDmfaVoorWerknemerService implements GiveDmfaVoorWerknemerService {

    private final MagdaClient service;
    private final MagdaResponseDmfaVoorWerknemerAdapter adapter;

    public MagdaClientGiveDmfaVoorWerknemerService(
            MagdaClient service,
            MagdaResponseDmfaVoorWerknemerAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public DmfaAttest getDmfaAttests(GeefDmfaVoorWerknemerRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }

    @Override
    public WithMagdaResponse<DmfaAttest> getDmfaAttestsWithSourceResponse(GeefDmfaVoorWerknemerRequest request) throws MagdaClientException {
        var magdaResponseWrapper = service.send(request);

        return new WithMagdaResponse<>(adapter.adapt(magdaResponseWrapper), magdaResponseWrapper.getResponse());
    }
}
