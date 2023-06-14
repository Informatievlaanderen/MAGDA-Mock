package be.vlaanderen.vip.magda.client.security;

import java.io.Serial;

public class TwoWaySslException extends Exception {
    @Serial
    private static final long serialVersionUID = 2049366580523981449L;

    public TwoWaySslException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoWaySslException(String message) {
        super(message);
    }
}
