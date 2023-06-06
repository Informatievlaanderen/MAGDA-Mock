package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijving0201Aanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.TypeInschrijving;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class RepertoriumTest extends MockTestBase {

    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_REPERTORIUM_FOUT = "91610100275";
    private static final String INSZ_REPERTORIUM_OK = "67021546719";

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200Lukt() {
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ_REPERTORIUM_OK, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/Resultaat", "1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ_REPERTORIUM_FOUT, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201Lukt() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ_REPERTORIUM_OK, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201OndernemingLukt() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.ONDERNEMING, "123456789012", LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietWegensInhoudelijkProbleem() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ_REPERTORIUM_FOUT, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());


        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietOmdatMagdaOverbelastIs() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200Lukt() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(INSZ_REPERTORIUM_OK, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);
        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(INSZ_REPERTORIUM_FOUT, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, aanvraag.getRequestId().toString());

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

}
