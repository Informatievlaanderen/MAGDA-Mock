package be.vlaanderen.vip.magda.client.security;

import java.io.Serial;

public class InvalidSignatureException extends Exception {
    @Serial
    private static final long serialVersionUID = -2739740446870263234L;

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSignatureException(String message) {
        super(message);
    }
}
