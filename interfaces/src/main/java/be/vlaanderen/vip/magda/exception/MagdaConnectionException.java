package be.vlaanderen.vip.magda.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * An exception that occurs while connecting to a MAGDA server.
 */
@Getter
public class MagdaConnectionException extends Exception {
    @Serial
    private static final long serialVersionUID = 6073479438535887564L;
    
    private final int statusCode;

    public MagdaConnectionException(String message, Exception cause) {
        super(message, cause);
        statusCode = 0;
    }

    public MagdaConnectionException(String message, int statusCode, Exception cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public MagdaConnectionException(String message) {
        super(message);
        statusCode = 0;
    }

    public MagdaConnectionException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
