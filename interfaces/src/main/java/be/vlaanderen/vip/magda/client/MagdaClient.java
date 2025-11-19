package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;

import java.util.UUID;

public interface MagdaClient {

    MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException;

    MagdaResponseWrapper send(MagdaRequest request, UUID requestId) throws MagdaClientException;

    MagdaResponseJson sendRestRequest(MagdaRestRequest request) throws MagdaClientException;
}
