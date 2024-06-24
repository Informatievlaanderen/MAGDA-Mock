package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefLeefLoonBedragenRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefLeefLoonBedragenTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefLeefLoonBedragenGeeftAntwoord() {
        final var requestInsz = "67722499797";
        var request = GeefLeefLoonBedragenRequest.builder()
                .insz(requestInsz)
                .currentYear(Year.of(2023))
                .numberOfYearsAgo(2)
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

        var inszNumbers = doc.getValues("//LeefloonPeriode/Begunstigde/INSZ");
        assertThat(inszNumbers).contains(requestInsz);
    }


}
