package be.vlaanderen.vip.magda.client.security;

public class InvalidSignatureException extends Exception {

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSignatureException(String message) {
        super(message);
    }
}
