package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefPersoonTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefPersoonGeeftAntwoord() {
        final var requestInsz = "00600099507";
        var request = new GeefPersoonRequest(requestInsz);
        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var voornaam = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/Naam/Voornamen/Voornaam");
        assertThat(voornaam).isEqualTo("Hamid");
    }


}
