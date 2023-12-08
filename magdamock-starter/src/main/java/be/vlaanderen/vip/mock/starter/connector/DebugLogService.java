package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.legallogging.model.FailedLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.SucceededLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.MagdaLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.UnansweredLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugLogService implements ClientLogService {

    @Override
    public void logMagdaRequest(MagdaLoggedRequest request) {
        if(log.isDebugEnabled()) {
            log.debug("Request sent: {}", request);
        }
    }

    @Override
    public void logFailedRequest(FailedLoggedRequest failedLoggedRequest) {
        if(log.isDebugEnabled()) {
            log.debug("Request failed: {}", failedLoggedRequest);
        }
    }

    @Override
    public void logSucceededRequest(SucceededLoggedRequest request) {
        if(log.isDebugEnabled()) {
            log.debug("Request succeeded: {}", request);
        }
    }

    @Override
    public void logUnansweredRequest(UnansweredLoggedRequest unansweredRequest) {
        if(log.isDebugEnabled()) {
            log.debug("Request unanswered: {}", unansweredRequest);
        }
    }

}
