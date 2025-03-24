package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;

import java.util.Optional;
import java.util.UUID;

public class MagdaClientGeefBewijzenService implements GeefBewijzenService {

    private final MagdaClient client;
    private final MagdaResponseBewijzenAdapter adapter;

    public MagdaClientGeefBewijzenService(
            MagdaClient client,
            MagdaResponseBewijzenAdapter adapter) {
        this.client = client;
        this.adapter = adapter;
    }

    public MagdaClientGeefBewijzenService(
            MagdaClient service) {
        this(service, MagdaResponseBewijzenAdapterJaxbImpl.getInstance());
    }

    @Override
    public Optional<Bewijzen> geefBewijzen(GeefBewijsRequest request) throws MagdaClientException {
        return adapter.adapt(client.send(request));
    }

    @Override
    public Optional<Bewijzen> geefBewijzen(GeefBewijsRequest request, UUID requestId) throws MagdaClientException {
        return adapter.adapt(client.send(request, requestId));
    }
}
