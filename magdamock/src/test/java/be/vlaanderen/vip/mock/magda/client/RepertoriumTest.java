package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijving0201Request;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingRequest;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepertoriumTest extends MockTestBase {

    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");
    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_REPERTORIUM_FOUT = "91610100275";
    private static final String INSZ_REPERTORIUM_OK = "67021546719";

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200Lukt() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_OK)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/Resultaat", "1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_FOUT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingEntries().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(UitzonderingType.FOUT);
        assertThat(uitzondering.getIdentification()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnosis()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201Lukt() {
        var request = RegistreerInschrijving0201Request.builder()
                .insz(INSZ_REPERTORIUM_OK)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201OndernemingLukt() {
        var request = RegistreerInschrijving0201Request.builder()
                .insz("123456789012")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerInschrijving0201Request.builder()
                .insz(INSZ_REPERTORIUM_FOUT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());


        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();


        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingv0201LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerInschrijving0201Request.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingEntries().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(UitzonderingType.FOUT);
        assertThat(uitzondering.getIdentification()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnosis()).isEqualTo("Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200Lukt() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_OK)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);
        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietWegensInhoudelijkProbleem() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_REPERTORIUM_FOUT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), RepertoriumTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void registreerUitschrijvingv0200LuktNietOmdatMagdaOverbelastIs() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
                .build();

        var afnemerLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingEntries().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(UitzonderingType.FOUT);
        assertThat(uitzondering.getIdentification()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnosis()).isEqualTo("Te veel gelijktijdige bevragingen");
    }
}
