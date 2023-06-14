package be.vlaanderen.vip.magda.exception;

import java.io.Serial;

// TODO - TEMP: to be replaced by version from Wwoom Common
public class BronException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3155129158010790297L;

    public BronException(String message) {
        super(message);
    }

    public BronException(String message, Throwable cause) {
        super(message, cause);
    }
}
