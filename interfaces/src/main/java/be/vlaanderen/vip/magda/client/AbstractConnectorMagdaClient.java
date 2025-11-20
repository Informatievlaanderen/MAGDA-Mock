package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.ServerException;

import java.util.UUID;

public abstract class AbstractConnectorMagdaClient implements MagdaClient {

    private final MagdaConnector connector;

    protected AbstractConnectorMagdaClient(
            MagdaConnector connector) {
        this.connector = connector;
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException {
        return send(request, UUID.randomUUID());
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request, UUID requestId) throws MagdaClientException {
        try {
            var response = connector.send(request, requestId);

            validateMagdaResponse(response, request);

            return new MagdaResponseWrapper(response);
        }
        catch (ServerException e) {
            throw new MagdaClientException("Error occurred while sending magda request", e);
        }
    }

    @Override
    public MagdaResponseJson sendRestRequest(MagdaRestRequest request) throws MagdaClientException{
        try {
            return connector.sendRestRequest(request);
        } catch (ServerException e) {
            throw new MagdaClientException("Error occurred while sending magda REST request", e);
        }
    }

    protected abstract void validateMagdaResponse(MagdaResponse response, MagdaRequest request) throws MagdaClientException;
}
