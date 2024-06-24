package be.vlaanderen.vip.magda.client;

import java.util.UUID;

public interface MagdaClient {

    MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException;

    MagdaResponseWrapper send(MagdaRequest request, UUID requestId) throws MagdaClientException;
}
