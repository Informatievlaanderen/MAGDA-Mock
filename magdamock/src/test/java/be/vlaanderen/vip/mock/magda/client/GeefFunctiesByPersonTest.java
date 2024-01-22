package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefFunctiesByPersonRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefFunctiesByPersonTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefFunctiesByPersonGeeftAntwoord() {
        final var requestInsz = "00630300287";
        var request = GeefFunctiesByPersonRequest.builder()
                .insz(requestInsz)
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

        var inszNumber = doc.getValue("//Functie/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);
    }


}
