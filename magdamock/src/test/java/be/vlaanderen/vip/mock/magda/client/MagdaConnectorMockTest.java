package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaAntwoord;
import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsMock;
import be.vlaanderen.vip.magda.exception.BronException;
import be.vlaanderen.vip.mock.magda.client.aanvraag.GeefPersoonAanvraag;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MagdaConnectorMockTest {
    @Test
    void geefPersoonGeeftAntwoord() {
        var connection = new MagdaMockConnection();
        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();
        MagdaEndpointsMock magdaEndpoints = new MagdaEndpointsMock();
        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid("Magda Mock", "magdamock.service", "123");
        MagdaHoedanigheidServiceMock magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);
        var connector = new MagdaConnectorMock(connection, afnemerLogService, magdaEndpoints, magdaHoedanigheidService);

        var aanvraag = new GeefPersoonAanvraag("00000099504");
        var antwoord = geefPersoon(aanvraag, connector);

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        log.info("{}",getSoap(antwoord.getBody()));

    }

    public MagdaAntwoord geefPersoon(GeefPersoonAanvraag aanvraag, MagdaConnector magdaConnector) {
        MagdaDocument request = MagdaDocument.fromResource(this.getClass(), "/templates/GeefPersoon/02.02.0000/template.xml");
        fillInParameters(aanvraag, request);
        try {
            return magdaConnector.send(aanvraag, request);
        } catch (Exception e) {
            throw new BronException(e.getMessage(), e);
        }
    }

    private void fillInParameters(GeefPersoonAanvraag aanvraag, MagdaDocument request) {
        request.setValue("//INSZ", aanvraag.getOverWie());
    }

    private String getSoap(Node response) {
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(response), new StreamResult(writer));
            String rawReturn = writer.toString();
            return rawReturn
                    .replaceAll("\r\n", "")
                    .replaceAll("\\s{2,}", " ")
                    .trim();
        } catch (TransformerException e) {
            return "Error during parsing soap response.";
        }
    }
}
