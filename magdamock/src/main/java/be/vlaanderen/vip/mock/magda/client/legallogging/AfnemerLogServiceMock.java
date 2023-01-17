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
    private int nietgeautoriseerd = 0;
    private int validatiefout = 0;

    private final List<MagdaAanvraag> aanvragenLijst = new ArrayList<>();
    private final List<GeslaagdeAanvraag> geslaagdeAanvragenLijst = new ArrayList<>();
    private final List<OnbeantwoordeAanvraag> onbeantwoordeAanvragenLijst = new ArrayList<>();
    private final List<GefaaldeAanvraag> gefaaldeAanvragenLijst = new ArrayList<>();

    @Override
    public synchronized void logAanvraag(MagdaAanvraag aanvraag) {
        log.info("Aanvraag: {}", aanvraag);
        aanvragenLijst.add(aanvraag);
    }

    @Override
    public synchronized void logGeslaagdeAanvraag(GeslaagdeAanvraag aanvraag) {
        log.info("Geslaagd: {}", aanvraag);
        geslaagdeAanvragenLijst.add(aanvraag);
    }

    @Override
    public synchronized void logGefaaldeAanvraag(GefaaldeAanvraag aanvraag) {
        log.info("Gefaald: {}", aanvraag);
        gefaaldeAanvragenLijst.add(aanvraag);
    }

    @Override
    public synchronized void logOnbeantwoordeAanvraag(OnbeantwoordeAanvraag aanvraag) {
        log.info("Onbeantwoord: {}", aanvraag);
        onbeantwoordeAanvragenLijst.add(aanvraag);
    }

    public int getAanvragen() {
        return this.aanvragenLijst.size();
    }

    public int getGeslaagd() {
        return geslaagdeAanvragenLijst.size();
    }

    public int getOnbeantwoord() {
        return this.onbeantwoordeAanvragenLijst.size();
    }

    public int getGefaald() {
        return gefaaldeAanvragenLijst.size();
    }

    public int getNietgeautoriseerd() {
        return this.nietgeautoriseerd;
    }

    public int getValidatiefout() {
        return this.validatiefout;
    }

    public List<MagdaAanvraag> getAanvragenLijst() {
        return this.aanvragenLijst;
    }

    public List<GeslaagdeAanvraag> getGeslaagdeAanvragenLijst() {
        return this.geslaagdeAanvragenLijst;
    }

    public List<OnbeantwoordeAanvraag> getOnbeantwoordeAanvragenLijst() {
        return this.onbeantwoordeAanvragenLijst;
    }

    public List<GefaaldeAanvraag> getGefaaldeAanvragenLijst() {
        return this.gefaaldeAanvragenLijst;
    }

    public void assertConsistent() {
        assertElkeAanvraagHeeftAntwoord();
        assertGeenDubbeleAntwoorden();
        assertGeenDubbeleOnbeantwoorden();
        assertReferteIsNietTransactie();
    }

    private void assertReferteIsNietTransactie() {
        aanvragenLijst.stream().forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        geslaagdeAanvragenLijst.stream().forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        gefaaldeAanvragenLijst.stream().forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
        onbeantwoordeAanvragenLijst.stream().forEach(AfnemerLogServiceMock::assertReferentieverschillendVanTransactie);
    }

    private static void assertReferentieverschillendVanTransactie(GelogdeAanvraag antwoord) {
        if (antwoord.getLocalTransactieID() == antwoord.getTransactieID()) {
            throw new IllegalStateException(String.format("Antwoord met referentie %s voor service %s door INSZ %s voor INSZ %s heeft referentie == transactie",
                    antwoord.getLocalTransactieID().toString(),
                    antwoord.getDienst(),
                    antwoord.getInsz(),
                    antwoord.getOverWie()));
        }
    }

    private void assertGeenDubbeleAntwoorden() {
        Set<String> antwoorden = new HashSet<>();
        for (GeslaagdeAanvraag antwoord : geslaagdeAanvragenLijst) {
            String content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Antwoord met referentie %s voor service %s door INSZ %s voor INSZ %s komt meerdere keren voor",
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
        for (OnbeantwoordeAanvraag antwoord : onbeantwoordeAanvragenLijst) {
            String content = inhoudVan(antwoord);
            if (antwoorden.contains(content)) {
                throw new IllegalStateException(String.format("Onbeantwoord met referentie %s voor service %s door INSZ %s voor INSZ %s komt meerdere keren voor",
                        antwoord.getLocalTransactieID().toString(),
                        antwoord.getDienst(),
                        antwoord.getInsz(),
                        antwoord.getOverWie()));
            }
            antwoorden.add(content);
        }
    }


    private String inhoudVan(GelogdeAanvraag antwoord) {
        return antwoord.getDienst() + "/" +
                antwoord.getTransactieID().toString() + "/" +
                antwoord.getLocalTransactieID().toString() + "/" +
                antwoord.getInsz() + "/" +
                antwoord.getOverWie();
    }

    private void assertElkeAanvraagHeeftAntwoord() {
        for (MagdaAanvraag aanvraag : aanvragenLijst) {
            List<GeslaagdeAanvraag> matchingGeslaagd = matchVoorAntwoord(aanvraag);
            List<OnbeantwoordeAanvraag> matchingOnbeantwoord = matchVoorOnbeantwoord(aanvraag);
            List<GefaaldeAanvraag> matchGefaald = matchVoorGefaald(aanvraag);
            if (!matchingGeslaagd.isEmpty() && !matchingOnbeantwoord.isEmpty() && !matchGefaald.isEmpty()) {
                dumpMe();
                throw new IllegalStateException(String.format("Aanvraag met referentie %s voor dienst %s door INSZ %s voor INSZ %s heeft zowel antwoorden als onbeantwoord als fouten",
                        aanvraag.getLocalTransactieID().toString(),
                        aanvraag.getDienst(),
                        aanvraag.getInsz(),
                        aanvraag.getOverWie()));
            }
            if (matchingGeslaagd.isEmpty() && matchingOnbeantwoord.isEmpty() && matchGefaald.isEmpty()) {
                dumpMe();
                throw new IllegalStateException(String.format("Aanvraag met referentie %s voor dienst %s door INSZ %s voor INSZ %s heeft noch antwoord noch onbeantwoord noch fout",
                        aanvraag.getLocalTransactieID().toString(),
                        aanvraag.getDienst(),
                        aanvraag.getInsz(),
                        aanvraag.getOverWie()));
            }
        }
    }

    private void dumpMe() {
        System.out.println("Vragen:");
        for (MagdaAanvraag aanvraag : getAanvragenLijst()) {
            System.out.println(String.format("Aanvraag %s %s %s %s %s", aanvraag.getDienst(), aanvraag.getInsz(), aanvraag.getOverWie(), aanvraag.getTransactieID().toString(), aanvraag.getLocalTransactieID().toString()));
        }
        System.out.println("Antwoorden:");
        for (GeslaagdeAanvraag aanvraag : getGeslaagdeAanvragenLijst()) {
            System.out.println(String.format("Aanvraag %s %s %s %s %s", aanvraag.getDienst(), aanvraag.getInsz(), aanvraag.getOverWie(), aanvraag.getTransactieID().toString(), aanvraag.getLocalTransactieID().toString()));
        }
        System.out.println("Fouten:");
        for (GefaaldeAanvraag aanvraag : getGefaaldeAanvragenLijst()) {
            System.out.println(String.format("Aanvraag %s %s %s %s %s", aanvraag.getDienst(), aanvraag.getInsz(), aanvraag.getOverWie(), aanvraag.getTransactieID().toString(), aanvraag.getLocalTransactieID().toString()));
        }
        System.out.println("Onbeantwoord:");
        for (OnbeantwoordeAanvraag aanvraag : getOnbeantwoordeAanvragenLijst()) {
            System.out.println(String.format("Aanvraag %s %s %s %s %s", aanvraag.getDienst(), aanvraag.getInsz(), aanvraag.getOverWie(), aanvraag.getTransactieID().toString(), aanvraag.getLocalTransactieID().toString()));
        }
    }

    public List<GefaaldeAanvraag> matchVoorGefaald(MagdaAanvraag aanvraag) {
        return this.gefaaldeAanvragenLijst.stream()
                .filter(antwoord -> match(aanvraag, antwoord))
                .toList();
    }

    public List<GeslaagdeAanvraag> matchVoorAntwoord(MagdaAanvraag vraag) {
        return geslaagdeAanvragenLijst.stream()
                .filter(antwoord -> match(vraag, antwoord))
                .toList();
    }

    public List<OnbeantwoordeAanvraag> matchVoorOnbeantwoord(MagdaAanvraag vraag) {
        return onbeantwoordeAanvragenLijst.stream()
                .filter(antwoord -> match(vraag, antwoord))
                .toList();
    }

    private boolean match(GelogdeAanvraag vraag, GelogdeAanvraag antwoord) {
        return vraag.getLocalTransactieID().equals(antwoord.getLocalTransactieID()) &&
                vraag.getTransactieID().equals(antwoord.getTransactieID()) &&
                vraag.getInsz().equals(antwoord.getInsz());
    }

    private void assertLogVoor(GelogdeAanvraag log, String insz) {
        assert insz.equals(log.getInsz()) : String.format("Log voor dienst %s bevat niet verwachte INSZ van vrager", log.getDienst());
    }

    public void assertAlleVragenEnAntwoordenVoor(String insz) {
        aanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        geslaagdeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        gefaaldeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
        onbeantwoordeAanvragenLijst.forEach(log -> assertLogVoor(log, insz));
    }
}
