package be.vlaanderen.vip.magda.exception;

public class TwoWaySslException extends Exception {
    private static final long serialVersionUID = 2049366580523981449L;

    public TwoWaySslException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoWaySslException(String message) {
        super(message);
    }
}
