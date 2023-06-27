package be.vlaanderen.vip.magda.legallogging.service;


import be.vlaanderen.vip.magda.legallogging.model.FailedLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.SucceededLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.MagdaLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.UnansweredLoggedRequest;

/**
 * An interface containing callbacks for events while processing a MAGDA request.
 * It is strongly recommend to create your own implementation for this class.
 */
public interface ClientLogService {

    /**
     * triggered before a request is sent
     *
     * @param magdaLoggedRequest the contents of the request
     */
    void logMagdaRequest(MagdaLoggedRequest magdaLoggedRequest);

    /**
     * triggered after a response has been received that does not contain errors
     *
     * @param succeededLoggedRequest the contents of the succeeded request
     */
    void logSucceededRequest(SucceededLoggedRequest succeededLoggedRequest);

    /**
     * triggered after a response has been received that contains errors
     *
     * @param failedLoggedRequest the contents of the failed request
     */
    void logFailedRequest(FailedLoggedRequest failedLoggedRequest);

    /**
     * triggered when the connection fails to obtain a (valid) response document
     *
     * @param unansweredLoggedRequest the contents of the unanswered request
     */
    void logUnansweredRequest(UnansweredLoggedRequest unansweredLoggedRequest);
}
