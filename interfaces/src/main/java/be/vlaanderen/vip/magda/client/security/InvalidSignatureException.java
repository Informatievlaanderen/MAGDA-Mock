package be.vlaanderen.vip.magda.client.security;

public class InvalidSignatureException extends Exception {
    private static final long serialVersionUID = -2739740446870263234L;

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSignatureException(String message) {
        super(message);
    }
}
