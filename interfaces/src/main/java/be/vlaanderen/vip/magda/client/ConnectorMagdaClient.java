package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.ServerException;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;

import java.util.UUID;

import static be.vlaanderen.vip.magda.client.Level3UitzonderingenSeverityChecks.*;

public class ConnectorMagdaClient implements MagdaClient {

    private final MagdaConnector connector;
    
    public ConnectorMagdaClient(
            MagdaConnector connector) {
        this.connector = connector;
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException {
        return send(request, UUID.randomUUID());
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request, UUID requestId) throws MagdaClientException {
        return send(request, requestId, SEVERE_ON_ERROR);
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request, Level3UitzonderingenSeverityCheck level3UitzonderingenSeverityCheck) throws MagdaClientException {
        return send(request, UUID.randomUUID(), level3UitzonderingenSeverityCheck);
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request, UUID requestId, Level3UitzonderingenSeverityCheck level3UitzonderingenSeverityCheck) throws MagdaClientException {
        try {
            var response = connector.send(request, requestId);

            validateMagdaResponse(response, request, level3UitzonderingenSeverityCheck);

            return new MagdaResponseWrapper(response);
        }
        catch (ServerException e) {
            throw new MagdaClientException("Error occurred while sending magda request", e);
        }
    }

    private void validateMagdaResponse(MagdaResponse response, MagdaRequest request, Level3UitzonderingenSeverityCheck level3UitzonderingenSeverityCheck) throws MagdaClientException {
        if(!response.getUitzonderingEntries().isEmpty()) {
            throw new MagdaClientException("Level 2 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getUitzonderingEntries(), request.getCorrelationId(), response.getRequestId()));
        }
        if(!response.getResponseUitzonderingEntries().isEmpty() && level3UitzonderingenSeverityCheck.isSevere(response.getResponseUitzonderingEntries())) {
            throw new MagdaClientException("Level 3 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getResponseUitzonderingEntries(), request.getCorrelationId(), response.getRequestId()));
        }
    }
}
