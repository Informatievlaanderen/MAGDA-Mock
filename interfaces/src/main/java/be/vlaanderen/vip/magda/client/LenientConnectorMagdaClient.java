package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;

/**
 * A MagdaConnector-based client which handles level 2 uitzonderingen in the response.
 */
public class LenientConnectorMagdaClient extends AbstractConnectorMagdaClient {

    public LenientConnectorMagdaClient(MagdaConnector connector) {
        super(connector);
    }

    @Override
    protected void validateMagdaResponse(MagdaResponse response, MagdaRequest request) throws MagdaClientException {
        if(!response.getUitzonderingEntries().isEmpty()) {
            throw new MagdaClientException("Level 2 exception occurred while calling magda service", new UitzonderingenSectionInResponseException(request.getSubject(), response.getUitzonderingEntries(), request.getCorrelationId(), response.getRequestId()));
        }
    }
}
