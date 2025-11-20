package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Document;

import java.net.URISyntaxException;

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

    /**
     * Sends a REST request and receives an according Json response document
     * @param request a DTO describing the REST request
     * @return the response document.
     */
    JsonNode sendRestRequest(MagdaRestRequest request) throws MagdaConnectionException, URISyntaxException;

    JsonNode sendRestRequest(String path, String query, String method, String requestBody) throws MagdaConnectionException;
}
