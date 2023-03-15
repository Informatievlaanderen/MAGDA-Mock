package be.vlaanderen.vip.magda.exception;

public class TwoWaySslException extends Exception {

    public TwoWaySslException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoWaySslException(String message) {
        super(message);
    }
}
