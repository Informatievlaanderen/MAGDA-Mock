package be.vlaanderen.vip.magda.exception;

import java.io.Serial;

/**
 * An exception that occurs in a communication with a MAGDA server.
 */
public class ServerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3155129158010790297L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
