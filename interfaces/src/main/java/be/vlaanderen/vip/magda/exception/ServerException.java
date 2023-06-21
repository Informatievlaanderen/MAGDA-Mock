package be.vlaanderen.vip.magda.exception;

import java.io.Serial;

// TODO - TEMP: to be replaced by version from Wwoom Common
public class ServerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3155129158010790297L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
