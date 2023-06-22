package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.ServerException;

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
}
