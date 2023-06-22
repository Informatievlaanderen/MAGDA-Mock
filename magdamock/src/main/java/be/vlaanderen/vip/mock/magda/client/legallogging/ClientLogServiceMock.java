package be.vlaanderen.vip.mock.magda.client.legallogging;

import be.vlaanderen.vip.magda.legallogging.model.*;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ClientLogServiceMock implements ClientLogService {

    private final List<MagdaLoggedRequest> magdaLoggedRequests = new ArrayList<>();
    private final List<SucceededLoggedRequest> succeededLoggedRequests = new ArrayList<>();
    private final List<UnansweredLoggedRequest> unansweredLoggedRequests = new ArrayList<>();
    private final List<FailedLoggedRequest> failedLoggedRequests = new ArrayList<>();

    @Override
    public synchronized void logMagdaRequest(MagdaLoggedRequest magdaLoggedRequest) {
        log.info("Request: {}", magdaLoggedRequest);
        magdaLoggedRequests.add(magdaLoggedRequest);
    }

    @Override
    public synchronized void logSucceededRequest(SucceededLoggedRequest succeededLoggedRequest) {
        log.info("Succeeded: {}", succeededLoggedRequest);
        succeededLoggedRequests.add(succeededLoggedRequest);
    }

    @Override
    public synchronized void logFailedRequest(FailedLoggedRequest failedLoggedRequest) {
        log.info("Failed: {}", failedLoggedRequest);
        failedLoggedRequests.add(failedLoggedRequest);
    }

    @Override
    public synchronized void logUnansweredRequest(UnansweredLoggedRequest unansweredLoggedRequest) {
        log.info("Unanswered: {}", unansweredLoggedRequest);
        unansweredLoggedRequests.add(unansweredLoggedRequest);
    }

    public int getNumberOfMagdaLoggedRequests() {
        return this.magdaLoggedRequests.size();
    }

    public int getNumberOfSucceededLoggedRequests() {
        return succeededLoggedRequests.size();
    }

    public int getNumberOfFailedLoggedRequests() {
        return failedLoggedRequests.size();
    }

    public List<MagdaLoggedRequest> getMagdaLoggedRequests() {
        return this.magdaLoggedRequests;
    }

    public List<SucceededLoggedRequest> getSucceededLoggedRequests() {
        return this.succeededLoggedRequests;
    }

    public List<UnansweredLoggedRequest> getUnansweredLoggedRequests() {
        return this.unansweredLoggedRequests;
    }

    public List<FailedLoggedRequest> getFailedLoggedRequests() {
        return this.failedLoggedRequests;
    }

    public void assertConsistent() {
        assertEachRequestHasAntwoord();
        assertGeenDubbeleAntwoorden();
        assertGeenDubbeleOnbeantwoorden();
        assertReferteIsNietTransactie();
    }

    private void assertReferteIsNietTransactie() {
        magdaLoggedRequests.forEach(ClientLogServiceMock::assertReferentieverschillendVanTransactie);
        succeededLoggedRequests.forEach(ClientLogServiceMock::assertReferentieverschillendVanTransactie);
        failedLoggedRequests.forEach(ClientLogServiceMock::assertReferentieverschillendVanTransactie);
        unansweredLoggedRequests.forEach(ClientLogServiceMock::assertReferentieverschillendVanTransactie);
    }

    private static void assertReferentieverschillendVanTransactie(LoggedRequest antwoord) {
        if (antwoord.getLocalTransactionID() == antwoord.getTransactionID()) {
            throw new IllegalStateException(String.format("Response with reference %s for service %s by INSZ %s for INSZ %s reference equals transaction",
                    antwoord.getLocalTransactionID().toString(),
                    antwoord.getServiceName(),
                    antwoord.getRequestingPartyInsz(),
                    antwoord.getSubjectInsz()));
        }
    }

    private void assertGeenDubbeleAntwoorden() {
        Set<String> antwoorden = new HashSet<>();
        for (var antwoord : succeededLoggedRequests) {
            var content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Response with reference %s for service %s by INSZ %s for INSZ %s has multiple occurrences",
                        antwoord.getLocalTransactionID().toString(),
                        antwoord.getServiceName(),
                        antwoord.getRequestingPartyInsz(),
                        antwoord.getSubjectInsz()));
            }
            antwoorden.add(content);
        }
    }


    private void assertGeenDubbeleOnbeantwoorden() {
        Set<String> antwoorden = new HashSet<>();
        for (var antwoord : unansweredLoggedRequests) {
            var content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Unanswered requests with reference %s for service %s by INSZ %s for INSZ %s has multiple occurrences",
                        antwoord.getLocalTransactionID().toString(),
                        antwoord.getServiceName(),
                        antwoord.getRequestingPartyInsz(),
                        antwoord.getSubjectInsz()));
            }
            antwoorden.add(content);
        }
    }


    private String inhoudVan(LoggedRequest antwoord) {
        return antwoord.getServiceName() + "/" +
                antwoord.getTransactionID().toString() + "/" +
                antwoord.getLocalTransactionID().toString() + "/" +
                antwoord.getRequestingPartyInsz() + "/" +
                antwoord.getSubjectInsz();
    }

    private void assertEachRequestHasAntwoord() {
        for (var request : magdaLoggedRequests) {
            var matchingGeslaagd = matchVoorAntwoord(request);
            var matchingOnbeantwoord = matchVoorOnbeantwoord(request);
            var matchGefaald = matchVoorGefaald(request);
            if (!matchingGeslaagd.isEmpty() && !matchingOnbeantwoord.isEmpty() && !matchGefaald.isEmpty()) {
                log.error("Illegal ClientLogServiceMock state");
                logState();
                throw new IllegalStateException(String.format("Request with reference %s for service %s by INSZ %s for INSZ %s has responses, as well as unanswered requests and errors",
                        request.getLocalTransactionID().toString(),
                        request.getServiceName(),
                        request.getRequestingPartyInsz(),
                        request.getSubjectInsz()));
            }
            if (matchingGeslaagd.isEmpty() && matchingOnbeantwoord.isEmpty() && matchGefaald.isEmpty()) {
                log.error("Illegal ClientLogServiceMock state");
                logState();
                throw new IllegalStateException(String.format("Request with reference %s for service %s by INSZ %s for INSZ %s has neither responses, nor unanswered requests, nor errors",
                        request.getLocalTransactionID().toString(),
                        request.getServiceName(),
                        request.getRequestingPartyInsz(),
                        request.getSubjectInsz()));
            }
        }
    }

    private void logState() {
        logLoggedRequests("Vragen:", getMagdaLoggedRequests());
        logLoggedRequests("Antwoorden:", getSucceededLoggedRequests());
        logLoggedRequests("Fouten:", getFailedLoggedRequests());
        logLoggedRequests("Onbeantwoord:", getUnansweredLoggedRequests());
    }

    private void logLoggedRequests(String header, Iterable<? extends LoggedRequest> loggedRequests) {
        log.debug(header);
        for (LoggedRequest loggedRequest : loggedRequests) {
            log.debug("Request {} {} {} {} {}",
                    loggedRequest.getServiceName(),
                    loggedRequest.getRequestingPartyInsz(),
                    loggedRequest.getSubjectInsz(),
                    loggedRequest.getTransactionID(),
                    loggedRequest.getLocalTransactionID());
        }
    }

    public List<FailedLoggedRequest> matchVoorGefaald(MagdaLoggedRequest magdaLoggedRequest) {
        return this.failedLoggedRequests.stream()
                .filter(antwoord -> match(magdaLoggedRequest, antwoord))
                .toList();
    }

    public List<SucceededLoggedRequest> matchVoorAntwoord(MagdaLoggedRequest magdaLoggedRequest) {
        return succeededLoggedRequests.stream()
                .filter(antwoord -> match(magdaLoggedRequest, antwoord))
                .toList();
    }

    public List<UnansweredLoggedRequest> matchVoorOnbeantwoord(MagdaLoggedRequest magdaLoggedRequest) {
        return unansweredLoggedRequests.stream()
                .filter(antwoord -> match(magdaLoggedRequest, antwoord))
                .toList();
    }

    private boolean match(LoggedRequest request, LoggedRequest antwoord) {
        return request.getLocalTransactionID().equals(antwoord.getLocalTransactionID()) &&
                request.getTransactionID().equals(antwoord.getTransactionID()) &&
                request.getRequestingPartyInsz().equals(antwoord.getRequestingPartyInsz());
    }

    private void assertLogFor(LoggedRequest log, String insz) {
        assert insz.equals(log.getRequestingPartyInsz()) : String.format("Log for service %s doesn't contain the expected INSZ of the requesting party", log.getServiceName());
    }

    public void assertAlleVragenEnAntwoordenVoor(String insz) {
        magdaLoggedRequests.forEach(log -> assertLogFor(log, insz));
        succeededLoggedRequests.forEach(log -> assertLogFor(log, insz));
        failedLoggedRequests.forEach(log -> assertLogFor(log, insz));
        unansweredLoggedRequests.forEach(log -> assertLogFor(log, insz));
    }
}
