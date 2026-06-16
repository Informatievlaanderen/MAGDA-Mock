package be.vlaanderen.vip.magda.exception;

import java.io.Serial;
import java.util.UUID;

/**
 * An exception that occurs in a communication with a MAGDA server.
 */
public class ServerException extends RuntimeException {

    private final UUID correlationID;
    private final UUID requestID;

    public UUID getCorrelationID() {
        return correlationID;
    }

    public UUID getRequestID() {
        return requestID;
    }

    @Serial
    private static final long serialVersionUID = -3155129158010790297L;

    public ServerException(String message, UUID correlationID, UUID requestID) {
        super(message);
        this.correlationID = correlationID;
        this.requestID = requestID;
    }

    public ServerException(String message, Throwable cause, UUID correlationID, UUID requestID) {
        super(message, cause);
        this.correlationID = correlationID;
        this.requestID = requestID;
    }
}
