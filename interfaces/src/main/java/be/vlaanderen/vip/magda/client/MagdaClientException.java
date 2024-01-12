package be.vlaanderen.vip.magda.client;

import java.io.Serial;

public class MagdaClientException extends Exception {

    @Serial
    private static final long serialVersionUID = -6422580446693505644L;

    public MagdaClientException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MagdaClientException(String message) {
        super(message);
    }
    
}
