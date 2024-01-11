package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;

import java.util.UUID;

public class MagdaClientGivePersonService implements GivePersonService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;
    
    public MagdaClientGivePersonService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    @Override
    public Person getPerson(GeefPersoonRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId))); // TODO abstract the correlation stuff somehow, maybe with a decorator pattern

        return new MagdaResponsePerson(service.send(request));
    }
}
