package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GeefBewijsTest extends MockTestBase {
      @Test
    @SneakyThrows
    void geefBewijsGeeftAntwoord() {
        final String requestInsz = "67621546751";
        var aanvraag = new GeefBewijsAanvraag(requestInsz);

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
        final String requestInsz = "57021546719";
        var aanvraag = new GeefBewijsAanvraag(requestInsz);

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen().size()).isEqualTo(1);
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var uitzondering = antwoord.getAntwoordUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("40000");
        assertThat(uitzondering.getOorsprong()).isEqualTo("LED");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Geen gegevens gevonden.");
    }
}
