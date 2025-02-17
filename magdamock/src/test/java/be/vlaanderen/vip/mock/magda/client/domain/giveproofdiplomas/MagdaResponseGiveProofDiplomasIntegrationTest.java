package be.vlaanderen.vip.mock.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.client.domain.giveproofdiplomas.ProofDiplomas;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MagdaResponseGiveProofDiplomasIntegrationTest {

    @Test
    void dataIsBoundToResponseDocument() throws MagdaClientException {
        var enrollmentHistory = proofDiplomas("90642300294");

        assertTrue(enrollmentHistory.isPresent());
        var bewijzen = enrollmentHistory.get().bewijzen();
        assertNotNull(bewijzen);
        assertEquals(1, bewijzen.size());
    }

    @Test
    void empty_ifResponseDocumentHasNoContent() throws MagdaClientException {
        var enrollmentHistory = proofDiplomas("00000000097");

        assertTrue(enrollmentHistory.isEmpty());
    }

    private Optional<ProofDiplomas> proofDiplomas(String insz) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefBewijsRequest.builder()
                .insz(insz)
                .build());

        return ProofDiplomas.ofMagdaDocument(response.getDocument());
    }
}