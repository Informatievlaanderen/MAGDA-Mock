package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDmfaVoorWerknemerRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefWerkrelatiesRequest;
import be.vlaanderen.vip.magda.client.domain.giveworkrelations.GiveWorkRelationsService;
import be.vlaanderen.vip.magda.client.domain.giveworkrelations.MagdaResponseWorkRelationsAdapter;
import be.vlaanderen.vip.magda.client.domain.giveworkrelations.WorkRelations;

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
}
