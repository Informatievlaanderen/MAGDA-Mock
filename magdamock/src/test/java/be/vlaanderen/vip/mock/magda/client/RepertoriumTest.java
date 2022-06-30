package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.XmlUtil;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijving0201Aanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.TypeInschrijving;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RepertoriumTest extends MockTestBase {
    @Test
    @SneakyThrows
    void registreerInschrijvingv0200Lukt() {
        final String requestInsz = "57021546719";
        var aanvraag = new RegistreerInschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        final String requestInsz = "91010100243";
        var aanvraag = new RegistreerInschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        final String requestInsz = "91010100144";
        var aanvraag = new RegistreerInschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0) ;
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT) ;
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996") ;
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen") ;
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201Lukt() {
        final String requestInsz = "57021546719";
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON,requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);


        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietWegensInhoudelijkProbleem() {
        final String requestInsz = "91010100243";
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON,requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);


        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietOmdatMagdaOverbelastIs() {
        final String requestInsz = "91010100144";
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON,requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0) ;
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT) ;
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996") ;
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen") ;
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200Lukt() {
        final String requestInsz = "57021546719";
        var aanvraag = new RegistreerUitschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        final String requestInsz = "91010100243";
        var aanvraag = new RegistreerUitschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        final String requestInsz = "91010100144";
        var aanvraag = new RegistreerUitschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("{}", request.toString());
        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0) ;
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT) ;
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996") ;
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen") ;
    }
}
