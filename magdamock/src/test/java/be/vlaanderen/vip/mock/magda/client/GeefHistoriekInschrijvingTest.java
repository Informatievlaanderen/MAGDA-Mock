package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefHistoriekInschrijvingTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefHistoriekInschrijvingenGeeftAntwoord() {
        final var requestInsz = "99691399711";
        var request = GeefHistoriekInschrijvingRequest.builder()
                .insz(requestInsz)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .build();
        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var ontvangerReferte = doc.getValue("//Bericht/Ontvanger/Referte");
        assertThat(ontvangerReferte).isEqualTo(REQUEST_ID.toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(REQUEST_ID.toString());

        var inschrijvingReferte = doc.getValue("//Inschrijvingen/Inschrijving/Referte");
        assertThat(inschrijvingReferte).isEqualTo("0bfbef2b-9fe4-4ffe-bc43-12e98308f2ed");
    }
}