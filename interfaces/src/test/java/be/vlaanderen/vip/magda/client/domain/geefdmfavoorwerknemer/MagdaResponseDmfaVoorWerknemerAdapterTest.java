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
        assertEquals(7, attesten.size());
        var attest = attesten.get(0);
        var identificatie = attest.identificatie();
        assertEquals("7847048323", identificatie.nummer());
        assertEquals("7847048323", identificatie.versie());
        var status = identificatie.status();
        assertEquals("0", status.codeValue());
        assertEquals("Nieuw", status.descriptionValue());
        assertEquals(LocalDate.of(2023, 10, 22), identificatie.datumCreatie());

        var aangifteWerkgever = attest.aangifteWerkgever();
        assertEquals("20233", aangifteWerkgever.kwartaal());
        assertEquals("8868157565", aangifteWerkgever.RSZNummer());
        assertEquals("RSZ", aangifteWerkgever.bron());
        assertEquals("PRI", aangifteWerkgever.sectorIndicator());
        assertEquals("0", aangifteWerkgever.onderCuratele());
        assertEquals("0454394421", aangifteWerkgever.ondernemingsNummer());
        assertEquals(970521, aangifteWerkgever.verschuldigdNettoBedrag());
        assertEquals("0", aangifteWerkgever.conversieNaarRegime5());

        var werknemer = aangifteWerkgever.werknemer();
        assertEquals("71640618918", werknemer.insz());
        assertEquals("0", werknemer.oriolusValidatie());

        var werknemerslijn = werknemer.werknemerslijn();
        assertEquals("10", werknemerslijn.categorie());
        assertEquals("495", werknemerslijn.kernGetal());
        assertEquals(LocalDate.of(2023, 7, 1), werknemerslijn.periode().begin());
        assertEquals(LocalDate.of(2023, 9, 30), werknemerslijn.periode().einde());
        assertEquals("0", werknemerslijn.grensarbeider());

        var tewerkstelling = werknemerslijn.tewerkstellingen().get(0);
        assertEquals("1", tewerkstelling.volgnummer());
        assertEquals("3303YCWFLQMZ", tewerkstelling.internNummer());
        assertEquals("2070345333", tewerkstelling.lokaleEenheid().nummer());
        assertEquals("31005", tewerkstelling.lokaleEenheid().nisCode());
        assertEquals(LocalDate.of(2020, 9, 1), tewerkstelling.periode().begin());
        assertEquals("200", tewerkstelling.paritairComite());
        assertEquals("500", tewerkstelling.aantalWerkdagenPerWeek());
        assertEquals("0", tewerkstelling.typeContract());
        assertEquals("4000", tewerkstelling.gemiddeldAantalUrenPerWeek().referentiePersoon());
        assertEquals("4000", tewerkstelling.gemiddeldAantalUrenPerWeek().werkNemer());
        assertEquals(24103, tewerkstelling.informatie().brutoLoonZiekte().intValue());

        var prestatie = tewerkstelling.prestaties().get(0);
        assertEquals("1", prestatie.volgnummer());
        assertEquals("1", prestatie.code());
        assertEquals("6500", prestatie.dagen());
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
