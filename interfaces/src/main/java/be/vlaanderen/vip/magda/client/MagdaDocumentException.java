package be.vlaanderen.vip.magda.client;

/**
 * An exception thrown when an error occurred while processing a MAGDA XML document, such as parsing failures.
 */
public class MagdaDocumentException extends RuntimeException {
    private static final long serialVersionUID = 1735099809337031155L;

    public MagdaDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}