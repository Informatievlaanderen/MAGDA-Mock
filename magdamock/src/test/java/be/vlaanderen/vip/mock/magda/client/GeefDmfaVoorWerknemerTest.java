package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefDmfaVoorWerknemerRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefWerkrelatiesRequest;
import be.vlaanderen.vip.magda.client.domain.dto.Kwartaal;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
class GeefDmfaVoorWerknemerTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    void GeefDmfaVoorWerknemerGeeftAntwoord() {
        final var requestInsz = "71640618918";
        final Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
        final Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
        var request = GeefDmfaVoorWerknemerRequest.builder()
                .insz(requestInsz)
                .beginKwartaal(beginKwartaal)
                .eindeKwartaal(eindeKwartaal)
                .bron(GeefDmfaVoorWerknemerRequest.Bron.DIBISS)
                .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeMagdaConnector(clientLogService);
        var antwoord = connector.send(request, REQUEST_ID);
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

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Attesten/Attest/AangifteWerkgever/Werknemer/INSZ");
        assertThat(insz).isEqualTo(requestInsz);
    }

    @Test
    void GeefDmfaVoorWerknemerGeeftGeenAntwoord() {
        final var requestInsz = "00000000000";
        final Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
        final Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
        var request = GeefDmfaVoorWerknemerRequest.builder()
                .insz(requestInsz)
                .beginKwartaal(beginKwartaal)
                .eindeKwartaal(eindeKwartaal)
                .bron(GeefDmfaVoorWerknemerRequest.Bron.DIBISS)
                .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeMagdaConnector(clientLogService);
        var antwoord = connector.send(request, REQUEST_ID);
        assertThat(antwoord.isBodyFilledIn()).isFalse();
        assertThat(antwoord.isHasContents()).isFalse();
        var uitzondering = antwoord.getResponseUitzonderingEntries().get(0);
        assertEquals(uitzondering.getDiagnosis(), "Geen gegevens gevonden voor de vraag");
        assertEquals(uitzondering.getIdentification(), "30001");
        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();
        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Attesten/Attest/AangifteWerkgever/Werknemer/INSZ");
        assertNull(insz);
    }
}
