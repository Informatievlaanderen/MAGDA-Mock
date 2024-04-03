package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

/**
 * The MAGDA server has not delivered a (valid) response.
 */
@Getter
public class NoResponseException extends ServerException {
    @Serial
    private static final long serialVersionUID = 4914331924177455934L;

    private final String serviceName;
    private final int statusCode;

    public NoResponseException(String bericht, Throwable oorzaak, MagdaRequest magdaRequest, int statusCode) {
        super(bericht, oorzaak, magdaRequest.getCorrelationId(), magdaRequest.getRequestId());
        this.serviceName = magdaRequest.magdaServiceIdentification().getName();
        this.statusCode = statusCode;
    }

    /**
     * @deprecated use {@link #getCorrelationID()} instead
     */
    @Deprecated
    public UUID getTransactionID() {
        return getCorrelationID();
    }

    /**
     * @deprecated use {@link #getRequestID()} instead
     */
    @Deprecated
    public UUID getLocalTransactionID() {
        return getRequestID();
    }
}
