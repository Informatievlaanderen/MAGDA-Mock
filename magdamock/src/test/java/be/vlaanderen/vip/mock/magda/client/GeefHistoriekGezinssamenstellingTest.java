package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekGezinssamenstellingRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefHistoriekGezinssamenstellingTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefHistoriekGezinssamenstellingGeeftAntwoord() {
        final var requestInsz = "67722499797";
        var request = GeefHistoriekGezinssamenstellingRequest.builder()
                .insz(requestInsz)
                .onDate(LocalDate.of(2023, 12, 1))
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

        var inszNumbers = doc.getValues("//Antwoorden/Antwoord/Inhoud/HistoriekGezinssamenstelling/Gezinssamenstelling/Gezinsleden/Gezinslid/INSZ");
        assertThat(inszNumbers).contains(requestInsz);
    }


}
