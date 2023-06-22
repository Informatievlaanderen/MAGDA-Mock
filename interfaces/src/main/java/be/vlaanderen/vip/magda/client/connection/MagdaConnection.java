package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import org.w3c.dom.Document;

/**
 * A document-level interface for exchanging request documents for response documents.
 */
public interface MagdaConnection {

    /**
     * Sends a request document and a receives an according response document.
     *
     * @param xml the request document.
     * @return the response document.
     * @throws MagdaConnectionException when the connection to the MAGDA server that handles the request fails.
     */
    Document sendDocument(Document xml) throws MagdaConnectionException;
}
