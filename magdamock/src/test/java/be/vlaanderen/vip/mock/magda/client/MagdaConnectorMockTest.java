package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.XmlUtil;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.endpoints.MagdaEndpointsMock;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MagdaConnectorMockTest {
    @Test
    @SneakyThrows
    void geefPersoonGeeftAntwoord() {
        final String requestInsz = "00000099504";
        var aanvraag = new GeefPersoonAanvraag(requestInsz);
        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var voornaam = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/Naam/Voornamen/Voornaam");
        assertThat(voornaam).isEqualTo("Hamid");
    }


    @Test
    @SneakyThrows
    void geefBewijsGeeftAntwoord() {
        final String requestInsz = "67021546719";
        var aanvraag = new GeefBewijsAanvraag(requestInsz);

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        log.info("{}", XmlUtil.toString(antwoord.getBody()));

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

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen().size()).isEqualTo(1);
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var uitzondering = antwoord.getAntwoordUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("40000");
        assertThat(uitzondering.getOorsprong()).isEqualTo("LED");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Geen gegevens gevonden.");
    }

    @Test
    @SneakyThrows
    void registreerInschrijvingLuktAltijd() {
        final String requestInsz = "57021546719";
        var aanvraag = new RegistreerInschrijvingAanvraag(requestInsz, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        // Voorbeeld van hoe de aanvraag gecustomizeerd wordt met specifieke parameters
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Start", aanvraag.getStart().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", aanvraag.getEinde().format(dateFormatter));

        var antwoord = connector.send(aanvraag, request);

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

    }

    private MagdaConnectorImpl makeMagdaConnector(AfnemerLogServiceMock afnemerLogService) {
        var connection = new MagdaMockConnection();
        MagdaEndpointsMock magdaEndpoints = new MagdaEndpointsMock();
        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid("Magda Mock", "magdamock.service", "123");
        MagdaHoedanigheidServiceMock magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);
        var connector = new MagdaConnectorImpl(connection, afnemerLogService, magdaEndpoints, magdaHoedanigheidService);
        return connector;
    }


}
