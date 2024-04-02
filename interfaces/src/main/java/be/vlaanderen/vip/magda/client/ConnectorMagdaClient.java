package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.ServerException;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;

public class ConnectorMagdaClient implements MagdaClient {
    private MagdaConnector connector;
    
    public ConnectorMagdaClient(
            MagdaConnector connector) {
        this.connector = connector;
    }

    @Override
    public MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException {
        try {
            var response = connector.send(request);
            
            validateMagdaResponse(response, request);
            
            return new MagdaResponseWrapper(response);
        }
        catch (ServerException e) {
            throw new MagdaClientException("Error occurred while sending magda request", e);
        }
    }
    
    private void validateMagdaResponse(MagdaResponse response, MagdaRequest request) throws MagdaClientException {
        if(!response.getUitzonderingEntries().isEmpty()) {
            throw new MagdaClientException("Level 2 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getUitzonderingEntries(), request.getRequestId()));
        }
        if(!response.getResponseUitzonderingEntries().isEmpty()) {
            throw new MagdaClientException("Level 3 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getResponseUitzonderingEntries(), request.getRequestId()));
        }
    }
    
}
