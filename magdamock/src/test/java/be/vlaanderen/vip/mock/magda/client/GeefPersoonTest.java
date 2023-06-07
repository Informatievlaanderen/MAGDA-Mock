package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonAanvraag;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GeefPersoonTest extends MockTestBase {
    @Test
    @SneakyThrows
    void geefPersoonGeeftAntwoord() {
        final String requestInsz = "00600099507";
        var aanvraag = new GeefPersoonAanvraag(requestInsz);
        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var voornaam = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/Naam/Voornamen/Voornaam");
        assertThat(voornaam).isEqualTo("Hamid");
    }


}
