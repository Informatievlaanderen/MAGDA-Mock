package be.vlaanderen.vip.mock.magdaservice.exception;

public class InitializationException extends RuntimeException {
    private static final long serialVersionUID = 1296389716903213831L;

    public InitializationException(String message, Throwable e) {
        super(message, e);
    }
    
}
