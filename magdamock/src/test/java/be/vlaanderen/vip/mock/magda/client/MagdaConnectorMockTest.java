package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.XmlUtil;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsMock;
import be.vlaanderen.vip.mock.magda.client.aanvraag.GeefPersoonAanvraag;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MagdaConnectorMockTest {
    @Test
    @SneakyThrows
    void geefPersoonGeeftAntwoord() {
        var connection = new MagdaMockConnection();
        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();
        MagdaEndpointsMock magdaEndpoints = new MagdaEndpointsMock();
        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid("Magda Mock", "magdamock.service", "123");
        MagdaHoedanigheidServiceMock magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);
        var connector = new MagdaConnectorMock(connection, afnemerLogService, magdaEndpoints, magdaHoedanigheidService);

        final String requestInsz = "00000099504";
        var aanvraag = new GeefPersoonAanvraag(requestInsz);
        MagdaDocument request = MagdaDocument.fromResource(this.getClass(), "/templates/GeefPersoon/02.02.0000/template.xml");

        var antwoord = connector.send(aanvraag, request);

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        log.info("{}", XmlUtil.toString(antwoord.getBody()));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(antwoord.getBody(), true);
        newDocument.appendChild(importedNode);

        var doc = new MagdaDocument(newDocument);
        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString()) ;

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/INSZ");
        assertThat(insz).isEqualTo(requestInsz) ;

        var voornaam = doc.getValue("//Antwoorden/Antwoord/Inhoud/Persoon/Naam/Voornamen/Voornaam");
        assertThat(voornaam).isEqualTo("Hamid") ;
    }


}
