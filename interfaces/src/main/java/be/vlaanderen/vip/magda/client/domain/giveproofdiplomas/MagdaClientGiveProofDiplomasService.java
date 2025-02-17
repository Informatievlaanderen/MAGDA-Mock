package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;

import java.util.Optional;
import java.util.UUID;

public class MagdaClientGiveProofDiplomasService implements GiveProofDiplomasService {

    private final MagdaClient client;
    private final MagdaResponseProofDiplomasAdapter adapter;

    public MagdaClientGiveProofDiplomasService(
            MagdaClient client,
            MagdaResponseProofDiplomasAdapter adapter) {
        this.client = client;
        this.adapter = adapter;
    }

    public MagdaClientGiveProofDiplomasService(
            MagdaClient service) {
        this(service, MagdaResponseProofDiplomasAdapterJaxbImpl.getInstance());
    }

    @Override
    public Optional<ProofDiplomas> getProofDiplomas(GeefBewijsRequest request) throws MagdaClientException {
        return adapter.adapt(client.send(request));
    }

    @Override
    public Optional<ProofDiplomas> getProofDiplomas(GeefBewijsRequest request, UUID requestId) throws MagdaClientException {
        return adapter.adapt(client.send(request, requestId));
    }
}
