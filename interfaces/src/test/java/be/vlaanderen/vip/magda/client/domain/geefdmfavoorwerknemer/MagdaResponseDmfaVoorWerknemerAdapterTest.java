package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.TestHelpers;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MagdaResponseDmfaVoorWerknemerAdapterTest {

    private final MagdaResponseDmfaVoorWerknemerAdapter adapter = new MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl();

    @Test
    void producesEnterpriseFunctionsFromResponseXml() throws IOException, MagdaClientException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/dmfavoorwerknemers/sample.xml")))
                .build();

        var dmfaAttest = adapter.adapt(new MagdaResponseWrapper(magdaResponse));

        assertNotNull(dmfaAttest);
        var attesten = dmfaAttest.attesten();
        assertEquals(attesten.size(), 7);
        var attest = attesten.get(0);
        var identificatie = attest.identificatie();
        assertEquals(identificatie.nummer(), "7847048323");
        assertEquals(identificatie.versie(), "7847048323");
        var status = identificatie.status();
        assertEquals(status.codeValue(), "0");
        assertEquals(status.descriptionValue(), "Nieuw");
        assertEquals(identificatie.datumCreatie(), LocalDate.of(2023, 10, 22));

        var aangifteWerkgever = attest.aangifteWerkgever();
        assertEquals(aangifteWerkgever.kwartaal(), "20233");
        assertEquals(aangifteWerkgever.RSZNummer(), "88681575651");
        assertEquals(aangifteWerkgever.bron(), "RSZ");
        assertEquals(aangifteWerkgever.sectorIndicator(), "PRI");
        assertEquals(aangifteWerkgever.onderCuratele(), "0");
        assertEquals(aangifteWerkgever.ondernemingsNummer(), "0454394421");
        assertEquals(aangifteWerkgever.verschuldigdNettoBedrag(), 970521);
        assertEquals(aangifteWerkgever.conversieNaarRegime5(), "0");

        var werknemer = aangifteWerkgever.werknemer();
        assertEquals(werknemer.insz(), "71640618918");
        assertEquals(werknemer.oriolusValidatie(), "0");

        var werknemerslijn = werknemer.werknemerslijn();
        assertEquals(werknemerslijn.categorie(), "10");
        assertEquals(werknemerslijn.kernGetal(), "495");
        assertEquals(werknemerslijn.periode().begin(), LocalDate.of(2023, 7, 1));
        assertEquals(werknemerslijn.periode().einde(), LocalDate.of(2023, 9, 30));
        assertEquals(werknemerslijn.grensarbeider(), "0");

        var tewerkstelling = werknemerslijn.tewerkstellingen().get(0);
        assertEquals(tewerkstelling.volgnummer(), "1");
        assertEquals(tewerkstelling.internNummer(), "3303YCWFLQMZ");
        assertEquals(tewerkstelling.lokaleEenheid().nummer(), "2070345333");
        assertEquals(tewerkstelling.lokaleEenheid().nisCode(), "31005");
        assertEquals(tewerkstelling.periode().begin(), LocalDate.of(2020, 9, 1));
        assertEquals(tewerkstelling.paritairComite(), "200");
        assertEquals(tewerkstelling.aantalWerkdagenPerWeek(), 500);
        assertEquals(tewerkstelling.typeContract(), "0");
        assertEquals(tewerkstelling.gemiddeldAantalUrenPerWeek().referentiePersoon(), 4000);
        assertEquals(tewerkstelling.gemiddeldAantalUrenPerWeek().werkNemer(), 4000);
        assertEquals(tewerkstelling.informatie().brutoLoonZiekte(), 24103);

        var prestatie = tewerkstelling.prestaties().get(0);
        assertEquals(prestatie.volgnummer(), "1");
        assertEquals(prestatie.code(), "1");
        assertEquals(prestatie.dagen(), 6500);
    }

    @Test
    void throwsErrorWhenNoResponse() throws IOException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/dmfavoorwerknemers/error.xml")))
                .build();

        try {
            adapter.adapt(new MagdaResponseWrapper(magdaResponse));
        } catch (MagdaClientException e) {
            assertEquals("Could not parse magda response", e.getMessage());
        }
    }
}
