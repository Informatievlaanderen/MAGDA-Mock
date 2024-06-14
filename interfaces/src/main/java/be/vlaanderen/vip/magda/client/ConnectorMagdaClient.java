package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.ServerException;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;

import java.util.List;

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
            throw new MagdaClientException("Level 2 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getUitzonderingEntries(), request.getCorrelationId(), request.getRequestId()));
        }
        if(!response.getResponseUitzonderingEntries().isEmpty() && haveAtLeastOneFout(response.getResponseUitzonderingEntries())) {
            throw new MagdaClientException("Level 3 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getResponseUitzonderingEntries(), request.getCorrelationId(), request.getRequestId()));
        }
    }

    private boolean haveAtLeastOneFout(List<UitzonderingEntry> entries) {
        return entries.stream().anyMatch(uitzonderingEntry -> uitzonderingEntry.getUitzonderingType().equals(UitzonderingType.FOUT));
    }
    
}
