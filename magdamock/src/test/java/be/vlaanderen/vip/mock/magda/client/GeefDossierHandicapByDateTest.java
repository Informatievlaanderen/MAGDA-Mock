package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefDossierHandicapByDateRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefDossierHandicapByDateTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefDossierHandicapByDateGeeftAntwoord() {
        final var requestInsz = "88681575651";
        var request = GeefDossierHandicapByDateRequest.builder()
                .insz(requestInsz)
                .referenceDate(LocalDate.of(2023, 1, 1))
                .sources(Set.of(
                        GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.DGPH
                ))
                .parts(Set.of(
                        GeefDossierHandicapByDateRequest.HandicapFilePartType.SOCIAL_CARDS
                ))
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

        var status = doc.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse/status/value");
        assertThat(status).isEqualTo("DATA_FOUND");
    }

    @Test
    @SneakyThrows
    void geefDossierHandicapByDateGeeftAntwoordNotFound() {
        final var requestInsz = "00010100173";
        var request = GeefDossierHandicapByDateRequest.builder()
                .insz(requestInsz)
                .referenceDate(LocalDate.of(2023, 1, 1))
                .sources(Set.of(
                        GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.DGPH
                ))
                .parts(Set.of(
                        GeefDossierHandicapByDateRequest.HandicapFilePartType.SOCIAL_CARDS
                ))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var status = doc.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse/status/value");
        assertThat(status).isEqualTo("NO_DATA_FOUND");
    }
}
