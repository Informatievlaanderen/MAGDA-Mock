package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;

import java.util.UUID;

public class MagdaClientSocialStatutesService implements SocialStatutesService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;

    public MagdaClientSocialStatutesService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }


    @Override
    public SocialStatutes getSocialStatutes(GeefMultipleSociaalStatuutRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId)));
        return new MagdaResponseSocialStatutes(service.send(request));
    }
}
