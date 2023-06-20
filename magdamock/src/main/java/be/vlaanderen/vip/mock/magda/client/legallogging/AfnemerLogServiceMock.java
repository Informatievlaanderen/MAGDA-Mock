package be.vlaanderen.vip.mock.magda.client.legallogging;

import be.vlaanderen.vip.magda.legallogging.model.*;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AfnemerLogServiceMock implements AfnemerLogService {

    private final List<MagdaLoggedRequest> aanvragenLijst = new ArrayList<>();
    private final List<SucceededLoggedRequest> geslaagdeAanvragenLijst = new ArrayList<>();
    private final List<UnansweredLoggedRequest> onbeantwoordeAanvragenLijst = new ArrayList<>();
    private final List<FailedLoggedRequest> gefaaldeAanvragenLijst = new ArrayList<>();

    @Override
    public synchronized void logMagdaRequest(MagdaLoggedRequest magdaLoggedRequest) {
        log.info("Request: {}", magdaLoggedRequest);
        aanvragenLijst.add(magdaLoggedRequest);
    }

    @Override
    public synchronized void logSucceededRequest(SucceededLoggedRequest aanvraag) {
        log.info("Succeeded: {}", aanvraag);
        geslaagdeAanvragenLijst.add(aanvraag);
    }

    @Override
    public synchronized void logFailedRequest(FailedLoggedRequest aanvraag) {
        log.info("Failed: {}", aanvraag);
        gefaaldeAanvragenLijst.add(aanvraag);
    }

    @Override
    public synchronized void logUnansweredRequest(UnansweredLoggedRequest aanvraag) {
        log.info("Unanswered: {}", aanvraag);
        onbeantwoordeAanvragenLijst.add(aanvraag);
    }

    public int getAanvragen() {
        return this.aanvragenLijst.size();
    }

    public int getGeslaagd() {
        return geslaagdeAanvragenLijst.size();
    }

    public int getGefaald() {
        return gefaaldeAanvragenLijst.size();
    }

    public List<MagdaLoggedRequest> getAanvragenLijst() {
        return this.aanvragenLijst;
    }

    public List<SucceededLoggedRequest> getGeslaagdeAanvragenLijst() {
        return this.geslaagdeAanvragenLijst;
    }

    public List<UnansweredLoggedRequest> getOnbeantwoordeAanvragenLijst() {
        return this.onbeantwoordeAanvragenLijst;
    }

    public List<FailedLoggedRequest> getGefaaldeAanvragenLijst() {
        return this.gefaaldeAanvragenLijst;
    }

    public void assertConsistent() {
        assertElkeAanvraagHeeftAntwoord();
        assertGeenDubbeleAntwoorden();
        assertGeenDubbeleOnbeantwoorden();
        assertReferteIsNietTransactie();
    }

    private void assertReferteIsNietTransactie() {
        aanvragenLijst.forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        geslaagdeAanvragenLijst.forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        gefaaldeAanvragenLijst.forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        onbeantwoordeAanvragenLijst.forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
    }

    private static void assertReferentieverschillendVanTransactie(LoggedRequest antwoord) {
        if (antwoord.getLocalTransactieID() == antwoord.getTransactieID()) {
            throw new IllegalStateException(String.format("Response with reference %s for service %s by INSZ %s for INSZ %s reference equals transaction",
                    antwoord.getLocalTransactieID().toString(),
                    antwoord.getDienst(),
                    antwoord.getInsz(),
                    antwoord.getOverWie()));
        }
    }

    private void assertGeenDubbeleAntwoorden() {
        Set<String> antwoorden = new HashSet<>();
        for (var antwoord : geslaagdeAanvragenLijst) {
            var content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Response with reference %s for service %s by INSZ %s for INSZ %s has multiple occurrences",
                        antwoord.getLocalTransactieID().toString(),
                        antwoord.getDienst(),
                        antwoord.getInsz(),
                        antwoord.getOverWie()));
            }
            antwoorden.add(content);
        }
    }


    private void assertGeenDubbeleOnbeantwoorden() {
        Set<String> antwoorden = new HashSet<>();
        for (var antwoord : onbeantwoordeAanvragenLijst) {
            var content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Unanswered requests with reference %s for service %s by INSZ %s for INSZ %s has multiple occurrences",
                        antwoord.getLocalTransactieID().toString(),
                        antwoord.getDienst(),
                        antwoord.getInsz(),
                        antwoord.getOverWie()));
            }
            antwoorden.add(content);
        }
    }


    private String inhoudVan(LoggedRequest antwoord) {
        return antwoord.getDienst() + "/" +
                antwoord.getTransactieID().toString() + "/" +
                antwoord.getLocalTransactieID().toString() + "/" +
                antwoord.getInsz() + "/" +
                antwoord.getOverWie();
    }

    private void assertElkeAanvraagHeeftAntwoord() {
        for (var aanvraag : aanvragenLijst) {
            var matchingGeslaagd = matchVoorAntwoord(aanvraag);
            var matchingOnbeantwoord = matchVoorOnbeantwoord(aanvraag);
            var matchGefaald = matchVoorGefaald(aanvraag);
            if (!matchingGeslaagd.isEmpty() && !matchingOnbeantwoord.isEmpty() && !matchGefaald.isEmpty()) {
                log.error("Illegal AfnemerLogServiceMock state");
                logState();
                throw new IllegalStateException(String.format("Request with reference %s for service %s by INSZ %s for INSZ %s has responses, as well as unanswered requests and errors",
                        aanvraag.getLocalTransactieID().toString(),
                        aanvraag.getDienst(),
                        aanvraag.getInsz(),
                        aanvraag.getOverWie()));
            }
            if (matchingGeslaagd.isEmpty() && matchingOnbeantwoord.isEmpty() && matchGefaald.isEmpty()) {
                log.error("Illegal AfnemerLogServiceMock state");
                logState();
                throw new IllegalStateException(String.format("Request with reference %s for service %s by INSZ %s for INSZ %s has neither responses, nor unanswered requests, nor errors",
                        aanvraag.getLocalTransactieID().toString(),
                        aanvraag.getDienst(),
                        aanvraag.getInsz(),
                        aanvraag.getOverWie()));
            }
        }
    }

    private void logState() {
        logLoggedRequests("Vragen:", getAanvragenLijst());
        logLoggedRequests("Antwoorden:", getGeslaagdeAanvragenLijst());
        logLoggedRequests("Fouten:", getGefaaldeAanvragenLijst());
        logLoggedRequests("Onbeantwoord:", getOnbeantwoordeAanvragenLijst());
    }

    private void logLoggedRequests(String header, Iterable<? extends LoggedRequest> loggedRequests) {
        log.debug(header);
        for (LoggedRequest loggedRequest : loggedRequests) {
            log.debug("Request {} {} {} {} {}",
                    loggedRequest.getDienst(),
                    loggedRequest.getInsz(),
                    loggedRequest.getOverWie(),
                    loggedRequest.getTransactieID(),
                    loggedRequest.getLocalTransactieID());
        }
    }

    public List<FailedLoggedRequest> matchVoorGefaald(MagdaLoggedRequest aanvraag) {
        return this.gefaaldeAanvragenLijst.stream()
                .filter(antwoord -> match(aanvraag, antwoord))
                .toList();
    }

    public List<SucceededLoggedRequest> matchVoorAntwoord(MagdaLoggedRequest vraag) {
        return geslaagdeAanvragenLijst.stream()
                .filter(antwoord -> match(vraag, antwoord))
                .toList();
    }

    public List<UnansweredLoggedRequest> matchVoorOnbeantwoord(MagdaLoggedRequest vraag) {
        return onbeantwoordeAanvragenLijst.stream()
                .filter(antwoord -> match(vraag, antwoord))
                .toList();
    }

    private boolean match(LoggedRequest vraag, LoggedRequest antwoord) {
        return vraag.getLocalTransactieID().equals(antwoord.getLocalTransactieID()) &&
                vraag.getTransactieID().equals(antwoord.getTransactieID()) &&
                vraag.getInsz().equals(antwoord.getInsz());
    }

    private void assertLogVoor(LoggedRequest log, String insz) {
        assert insz.equals(log.getInsz()) : String.format("Log voor dienst %s bevat niet verwachte INSZ van vrager", log.getDienst());
    }

    public void assertAlleVragenEnAntwoordenVoor(String insz) {
        aanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        geslaagdeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        gefaaldeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        onbeantwoordeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
    }
}
