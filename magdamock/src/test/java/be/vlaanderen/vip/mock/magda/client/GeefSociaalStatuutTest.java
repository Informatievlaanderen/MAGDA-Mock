package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefSociaalStatuutTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefSociaalStatuutGeeftAntwoord() {
        final var requestInsz = "62691699717";
        final var requestStatusName = "PCSA_AND_BIM_HELP";
        var request = GeefSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .date(LocalDate.now())
                .socialStatusName(requestStatusName)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusNames = doc.getValues("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusNames.size()).isEqualTo(1);
        assertThat(statusNames.getFirst()).isEqualTo(requestStatusName);

        var resultCode = doc.getValue("//SocialeStatuten/SociaalStatuut/Resultaat/Code");
        assertThat(resultCode).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void geefSociaalStatuutGeeftAntwoordNotFound() {
        final var requestInsz = "62691699717";
        final var requestStatusName = "SOCIAL_STATUTE_NAME";
        var request = GeefSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .date(LocalDate.now())
                .socialStatusName(requestStatusName)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusNames = doc.getValues("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusNames.size()).isEqualTo(1);
        assertThat(statusNames.getFirst()).isEqualTo(requestStatusName);

        var resultCode = doc.getValue("//SocialeStatuten/SociaalStatuut/Resultaat/Code");
        assertThat(resultCode).isEqualTo("0");
    }
}
