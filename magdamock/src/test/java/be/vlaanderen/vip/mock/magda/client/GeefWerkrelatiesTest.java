package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefWerkrelatiesRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefWerkrelatiesTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefWerkrelatiesGeeftAntwoord() {
        final var requestInsz = "00630300287";
        var request = GeefWerkrelatiesRequest.builder()
                .insz(requestInsz)
                .startDate(LocalDate.of(2023, 1,1))
                .endDate(LocalDate.of(2023,12,31))
                .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL)
                .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
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

        var insz = doc.getValue("//Contract/Relatie/Werknemer/INSZ");
        assertThat(insz).isEqualTo(requestInsz);
    }
}
