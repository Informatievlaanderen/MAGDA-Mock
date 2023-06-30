package be.vlaanderen.vip.magda.client;

/**
 * An exception thrown when an error occurred while processing a MAGDA XML document, such as parsing failures.
 */
public class MagdaDocumentException extends RuntimeException {

    public MagdaDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}