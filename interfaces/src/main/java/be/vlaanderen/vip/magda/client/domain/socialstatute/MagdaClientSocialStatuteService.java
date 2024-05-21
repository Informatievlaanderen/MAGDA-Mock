package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;

public class MagdaClientSocialStatuteService implements SocialStatuteService {

    private final MagdaClient service;
    private final MagdaResponseSocialStatutesAdapter adapter;

    public MagdaClientSocialStatuteService(
            MagdaClient service,
            MagdaResponseSocialStatutesAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    @Override
    public SocialStatutes getSocialStatutes(GeefSociaalStatuutRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }

    @Override
    public SocialStatutes getSocialStatutesByMultipleCriteria(GeefMultipleSociaalStatuutRequest request) throws MagdaClientException {
        return adapter.adapt(service.send(request));
    }
}