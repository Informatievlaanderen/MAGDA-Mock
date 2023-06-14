package be.vlaanderen.vip.mock.magda.client.exceptions;

/**
 * An exception to be thrown by MagdaMock in case MagdaMock really fails to handle a response,
 * rather than simulating a backend failing to handle a response.
 */
public class MagdaMockException extends RuntimeException {

    public MagdaMockException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagdaMockException(String message) {
        super(message);
    }
}