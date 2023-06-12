package be.vlaanderen.vip.mock.magdaservice.exception;

import java.io.Serial;

public class InitializationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1296389716903213831L;

    public InitializationException(String message, Throwable e) {
        super(message, e);
    }
}
