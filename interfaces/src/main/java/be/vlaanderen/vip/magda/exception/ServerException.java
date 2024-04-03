package be.vlaanderen.vip.magda.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

/**
 * An exception that occurs in a communication with a MAGDA server.
 */
@Getter
public class ServerException extends RuntimeException {

    private final UUID transactionID;
    private final UUID localTransactionID;

    @Serial
    private static final long serialVersionUID = -3155129158010790297L;

    public ServerException(String message, UUID transactionID, UUID localTransactionID) {
        super(message);
        this.transactionID = transactionID;
        this.localTransactionID = localTransactionID;
    }

    public ServerException(String message, Throwable cause,UUID transactionID, UUID localTransactionID) {
        super(message, cause);
        this.transactionID = transactionID;
        this.localTransactionID = localTransactionID;
    }
}
