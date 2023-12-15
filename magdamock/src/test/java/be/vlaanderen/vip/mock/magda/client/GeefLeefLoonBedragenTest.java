package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefLeefLoonBedragenRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefLeefLoonBedragenTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefLeefLoonBedragenGeeftAntwoord() {
        final var requestInsz = "83660634135";
        var request = GeefLeefLoonBedragenRequest.builder()
                .insz(requestInsz)
                .year(Year.of(2023))
                .currentYear(true)
                .numberOfYearsAgo(2)
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

        var inszNumbers = doc.getValues("//Antwoorden/Antwoord/Inhoud/Leefloonbedragen/Leefloonbedrag/Begunstigden/Begunstigde");
        assertThat(inszNumbers).contains(requestInsz);
    }


}
