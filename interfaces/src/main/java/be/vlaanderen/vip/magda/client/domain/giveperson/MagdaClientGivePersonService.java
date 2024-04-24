package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.correlation.NullCorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;

import java.util.UUID;

public class MagdaClientGivePersonService implements GivePersonService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;

    /**
     * @deprecated remove the correlationHeaderProvider parameters, and for all the relevant requests, replace all uses of a CorrelationHeaderProvider with request.setCorrelationId(correlationId)`.
     */
    @Deprecated(forRemoval = true)
    public MagdaClientGivePersonService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    @Deprecated(forRemoval = true)
    public MagdaClientGivePersonService(
            MagdaClient service) {
        this.service = service;
        this.correlationHeaderProvider = NullCorrelationHeaderProvider.getInstance();
    }

    @Override
    public Person getPerson(GeefPersoonRequest request) throws MagdaClientException {
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> request.setCorrelationId(UUID.fromString(xCorrelationId))); // TODO abstract the correlation stuff somehow, maybe with a decorator pattern

        return new MagdaResponsePerson(service.send(request));
    }
}
