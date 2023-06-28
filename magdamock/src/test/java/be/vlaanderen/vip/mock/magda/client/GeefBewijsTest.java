package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefBewijsTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefBewijsGeeftAntwoord() {
        final var requestInsz = "67621546751";
        var request = GeefBewijsRequest.builder()
                .insz(requestInsz)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var leverancier = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/Leverancier/Naam");
        assertThat(leverancier).isEqualTo("Volwassenenonderwijs");

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/PersoonUitgereikt/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var instantie = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/Basis/Instantie/Naam");
        assertThat(instantie).isEqualTo("Centrum voor Volwassenenonderwijs (CVO)");

        var onderwerp = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/Basis/Onderwerp/Naam");
        assertThat(onderwerp).isEqualTo("Basis koken");
    }

    @Test
    @SneakyThrows
    void geefBewijsVoorOnbekendePersoonGeeftGeenGegevensGevonden() {
        final var requestInsz = "57021546719";
        var request = GeefBewijsRequest.builder()
                .insz(requestInsz)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHasContents()).isFalse();
        assertThat(antwoord.getResponseUitzonderingEntries()).hasSize(1);
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var uitzondering = antwoord.getResponseUitzonderingEntries().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(UitzonderingType.FOUT);
        assertThat(uitzondering.getIdentification()).isEqualTo("40000");
        assertThat(uitzondering.getOrigin()).isEqualTo("LED");
        assertThat(uitzondering.getDiagnosis()).isEqualTo("Geen gegevens gevonden.");
    }
}
