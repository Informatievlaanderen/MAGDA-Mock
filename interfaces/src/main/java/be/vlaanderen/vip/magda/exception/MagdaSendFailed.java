package be.vlaanderen.vip.magda.exception;

import lombok.Getter;

@Getter
public class MagdaSendFailed extends Exception {

    private final int statusCode;

    public MagdaSendFailed(String explanation, Exception cause) {
        super(explanation, cause);
        statusCode = 0;
    }

    public MagdaSendFailed(String explanation, int statusCode, Exception cause) {
        super(explanation, cause);
        this.statusCode = statusCode;
    }

    public MagdaSendFailed(String explanation) {
        super(explanation);
        statusCode = 0;
    }

    public MagdaSendFailed(String explanation, int statusCode) {
        super(explanation);
        this.statusCode = statusCode;
    }
}
