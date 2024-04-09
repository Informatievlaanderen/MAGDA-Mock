package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefSociaalStatuutTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefSociaalStatuutGeeftAntwoord() {
        final var requestInsz = "66691666644";
        final var requestStatusName = "BIM_BVT";
        var request = GeefSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .date(LocalDate.now())
                .socialStatusName(requestStatusName)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request);
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
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusName = doc.getValue("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusName).isEqualTo(statusName);
    }

    @Test
    @SneakyThrows
    void geefSociaalStatuutGeeftAntwoordNotFound() {
        final var requestInsz = "66691666645";
        final var requestStatusName = "BIM_BVT";
        var request = GeefSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .date(LocalDate.now())
                .socialStatusName(requestStatusName)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request);
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
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusName = doc.getValue("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusName).isEqualTo(statusName);

        var resultCode = doc.getValue("//SocialeStatuten/SociaalStatuut/Resultaat/Code");
        assertThat(resultCode).isEqualTo("0");
    }
}
