package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.ServerException;

import java.util.UUID;

/**
 * An interface to send {@link MagdaRequest}s to MAGDA and receive a {@link MagdaResponse}s.
 * If something goes wrong, a {@link ServerException} may be thrown.
 */
public interface MagdaConnector {

    /**
     * Sends a MAGDA request.
     *
     * @param magdaRequest the request to MAGDA.
     * @return the response from MAGDA.
     * @throws ServerException an error that may be thrown in cases of no response or validation errors from the server.
     */
    MagdaResponse send(MagdaRequest magdaRequest) throws ServerException;

    /**
     * Sends a MAGDA request with a specific request ID.
     *
     * @param magdaRequest the request to MAGDA.
     * @param requestId the request ID.
     * @return the response from MAGDA.
     * @throws ServerException an error that may be thrown in cases of no response or validation errors from the server.
     */
    MagdaResponse send(MagdaRequest magdaRequest, UUID requestId) throws ServerException;

    /**
     * Sends a MAGDA REST request.
     * @param request the REST request to MAGDA.
     * @return the response from MAGDA.
     * @throws ServerException an error that may be thrown in cases of no response or validation errors from the server.
     */
    MagdaResponseJson sendRestRequest(MagdaRestRequest request) throws ServerException;

    /**
     * Sends a MAGDA REST request for a specific registration.
     * @param requestBuilder the builder for the REST request to MAGDA to which the details from the registration will be added.
     * @param registration the registration to be used with the request.
     * @return the response from MAGDA.
     * @throws ServerException an error that may be thrown in cases of no response or validation errors from the server.
     */
    MagdaResponseJson sendRestRequest(MagdaRestRequest.MagdaRestRequestBuilder requestBuilder, String registration) throws ServerException;
}
