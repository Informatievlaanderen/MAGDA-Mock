package be.vlaanderen.vip.magda.client;

import java.util.UUID;

public interface MagdaClient {

    MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException;

    MagdaResponseWrapper send(MagdaRequest request, UUID requestId) throws MagdaClientException;

    MagdaResponseWrapper send(MagdaRequest request, Level3UitzonderingenSeverityCheck level3UitzonderingenSeverityCheck) throws MagdaClientException;

    MagdaResponseWrapper send(MagdaRequest request, UUID requestId, Level3UitzonderingenSeverityCheck level3UitzonderingenSeverityCheck) throws MagdaClientException;
}
