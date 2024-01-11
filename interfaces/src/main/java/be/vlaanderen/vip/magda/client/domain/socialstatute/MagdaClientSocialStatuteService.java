package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;

import java.util.UUID;

public class MagdaClientSocialStatuteService implements SocialStatuteService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;
    
    public MagdaClientSocialStatuteService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    @Override
    public SocialStatute getSocialStatute(GeefSociaalStatuutRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId)));

        return new MagdaResponseSocialStatute(service.send(request));
    }
}