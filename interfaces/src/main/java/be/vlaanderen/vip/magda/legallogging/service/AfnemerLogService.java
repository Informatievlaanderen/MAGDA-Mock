package be.vlaanderen.vip.magda.legallogging.service;


import be.vlaanderen.vip.magda.legallogging.model.FailedLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.SucceededLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.MagdaLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.UnansweredLoggedRequest;

public interface AfnemerLogService {

    void logMagdaRequest(MagdaLoggedRequest magdaLoggedRequest);

    void logSucceededRequest(SucceededLoggedRequest succeededLoggedRequest);

    void logFailedRequest(FailedLoggedRequest failedLoggedRequest);

    void logUnansweredRequest(UnansweredLoggedRequest unansweredLoggedRequest);
}
