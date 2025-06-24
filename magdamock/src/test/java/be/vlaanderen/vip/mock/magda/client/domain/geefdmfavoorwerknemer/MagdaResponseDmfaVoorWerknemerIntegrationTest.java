package be.vlaanderen.vip.mock.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefDmfaVoorWerknemerRequest;
import be.vlaanderen.vip.magda.client.domain.dto.Kwartaal;
import be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer.DmfaAttest;
import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MagdaResponseDmfaVoorWerknemerIntegrationTest {

    private DmfaAttest dmfaAttest;

    @BeforeEach
    void setup() throws MagdaClientException {
        dmfaAttest = dmfaAttest("71640618918");
        assertNotNull(dmfaAttest);
    }

    @Test
    void mapsInszNummer() {
        assertEquals("71640618918", dmfaAttest.attesten().get(0).aangifteWerkgever().werknemer().insz());
    }

    @Test
    void mapsCompanyNumber() {
        assertEquals("0454394421", dmfaAttest.attesten().get(0).aangifteWerkgever().ondernemingsNummer());
    }

    @Test
    void mapsRSZNumber() {
        assertEquals("8868157565", dmfaAttest.attesten().get(0).aangifteWerkgever().RSZNummer());
    }

    @Test
    void mapsBron() {
        assertEquals("RSZ", dmfaAttest.attesten().get(0).aangifteWerkgever().bron());
    }

    @Test
    void mapsPeriode() {
        DmfaAttest.Periode periode = dmfaAttest.attesten().get(0).aangifteWerkgever().werknemer().werknemerslijn().periode();
        assertEquals(LocalDate.of(2023, 7, 1), periode.begin());
        assertEquals(LocalDate.of(2023, 9, 30), periode.einde());
    }

    @Test
    void mapsIdentificatie() {
        DmfaAttest.Identificatie identificatie = dmfaAttest.attesten().get(0).identificatie();
        assertEquals("7847048323", identificatie.nummer());
        assertEquals("7847048323", identificatie.versie());
        assertEquals("0", identificatie.status().codeValue());
        assertEquals("Nieuw", identificatie.status().descriptionValue());
        assertEquals(LocalDate.of(2023, 10, 22), identificatie.datumCreatie());
    }

    @Test
    void returnsNullIfEnterpriseIsAbsent() throws MagdaClientException {
        assertNull(dmfaAttest("0874713140"));
    }

    private DmfaAttest dmfaAttest(String insz) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefDmfaVoorWerknemerRequest.builder()
                .insz(insz)
                .beginKwartaal(new Kwartaal(0, 0))
                .eindKwartaal(new Kwartaal(1, 0))
                .build());

        return DmfaAttest.ofMagdaDocument(response.getDocument());
    }
}