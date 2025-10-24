package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDossierHandicapByDateRequest;
import be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer.MagdaResponseDmfaVoorWerknemerAdapter;

public class MagdaClientGiveDisabilityDossierService implements GiveDisabilityDossierService {
    private final MagdaClient service;
    private final MagdaResponseDisabilityDossierAdapter adapter;

    public MagdaClientGiveDisabilityDossierService(
            MagdaClient service,
            MagdaResponseDisabilityDossierAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public DisabilityDossier getDisabilityDossier(GeefDossierHandicapByDateRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}
