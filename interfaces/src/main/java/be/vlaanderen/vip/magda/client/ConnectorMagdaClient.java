package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;

import java.util.List;

/**
 * A MagdaConnector-based client which handles both level 2 and level 3 uitzonderingen in the response.
 * Level 3 uitzonderingen are handled in an opinionated manner whereby an exception is thrown if at least one of them is of type "FOUT".
 */
public class ConnectorMagdaClient extends AbstractConnectorMagdaClient {

    public ConnectorMagdaClient(MagdaConnector connector) {
        super(connector);
    }

    public ConnectorMagdaClient(MagdaConnector soapConnector,
                                MagdaConnector restConnector) {
        super(soapConnector, restConnector);
    }

    @Override
    protected void validateMagdaResponse(MagdaResponse response, MagdaRequest request) throws MagdaClientException {
        if(!response.getUitzonderingEntries().isEmpty()) {
            throw new MagdaClientException("Level 2 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getUitzonderingEntries(), request.magdaServiceIdentification(), request.getCorrelationId(), response.getRequestId()));
        }
        if(!response.getResponseUitzonderingEntries().isEmpty() && haveAtLeastOneFout(response.getResponseUitzonderingEntries())) {
            throw new MagdaClientException("Level 3 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getResponseUitzonderingEntries(), request.magdaServiceIdentification(), request.getCorrelationId(), response.getRequestId()));
        }
    }

    private boolean haveAtLeastOneFout(List<UitzonderingEntry> entries) {
        return entries.stream().anyMatch(uitzonderingEntry -> uitzonderingEntry.getUitzonderingType().equals(UitzonderingType.FOUT));
    }
}
