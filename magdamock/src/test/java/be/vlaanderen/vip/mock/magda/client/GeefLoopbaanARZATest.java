package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefLoopbaanARZARequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefLoopbaanARZATest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefLoopbaanARZAGeeftAntwoord() {
        final var requestInsz = "00630300287";
        var request = GeefLoopbaanARZARequest.builder()
                .insz(requestInsz)
                .startDate(LocalDate.of(2023, 1,1))
                .endDate(LocalDate.of(2023,12,31))
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

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Zelfstandige/INSZ");
        assertThat(insz).isEqualTo(requestInsz);
    }
}
