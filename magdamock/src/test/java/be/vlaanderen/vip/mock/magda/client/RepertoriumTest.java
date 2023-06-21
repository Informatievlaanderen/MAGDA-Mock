package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijving0201Request;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingRequest;
import be.vlaanderen.vip.magda.client.diensten.TypeInschrijving;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RepertoriumTest extends MockTestBase {

    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_REPERTORIUM_FOUT = "91610100275";
    private static final String INSZ_REPERTORIUM_OK = "67021546719";

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200Lukt() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_OK)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/Resultaat", "1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_FOUT)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201Lukt() {
        var request = RegistreerInschrijving0201Request.builder()
                .type(TypeInschrijving.PERSOON)
                .insz(INSZ_REPERTORIUM_OK)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201OndernemingLukt() {
        var request = RegistreerInschrijving0201Request.builder()
                .type(TypeInschrijving.PERSOON)
                .insz("123456789012")
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerInschrijving0201Request.builder()
                .type(TypeInschrijving.PERSOON)
                .insz(INSZ_REPERTORIUM_FOUT)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());


        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerInschrijving0201Request.builder()
                .type(TypeInschrijving.PERSOON)
                .insz(INSZ_MAGDA_OVERBELAST)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200Lukt() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_OK)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);
        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_FOUT)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .start(LocalDate.now())
                .einde(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

}
