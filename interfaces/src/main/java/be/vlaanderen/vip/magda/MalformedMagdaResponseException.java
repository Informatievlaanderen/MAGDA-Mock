package be.vlaanderen.vip.magda;

import be.vlaanderen.vip.magda.client.MagdaDocumentException;

import java.io.Serial;

/**
 * An exception thrown when an error occurred while processing a MAGDA XML document, such as parsing failures.
 */
public class MalformedMagdaResponseException extends MagdaDocumentException {
    @Serial
    private static final long serialVersionUID = -872910816034006955L;

    public MalformedMagdaResponseException(String message) {
        super(message);
    }
}