package be.vlaanderen.vip.mock.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Bewijzen;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MagdaResponseGeefBewijzenIntegrationTest {

    @Test
    void dataIsBoundToResponseDocument() throws MagdaClientException {
        var enrollmentHistory = bewijzen("90642300294");

        assertTrue(enrollmentHistory.isPresent());
        var bewijzen = enrollmentHistory.get().bewijzen();
        assertNotNull(bewijzen);
        assertEquals(1, bewijzen.size());
    }

    @Test
    void empty_ifResponseDocumentHasNoContent() throws MagdaClientException {
        var enrollmentHistory = bewijzen("00000000097");

        assertTrue(enrollmentHistory.isEmpty());
    }

    private Optional<Bewijzen> bewijzen(String insz) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefBewijsRequest.builder()
                .insz(insz)
                .build());

        return Bewijzen.ofMagdaDocument(response.getDocument());
    }
}