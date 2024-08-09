package be.vlaanderen.vip.mock.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory.EnrollmentHistory;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MagdaResponseGiveEnrollmentHistoryIntegrationTest {

    @Test
    void dataIsBoundToResponseDocument() throws MagdaClientException {
        var enrollmentHistory = enrollmentHistory("88611099807");

        assertTrue(enrollmentHistory.isPresent());
        var enrollments = enrollmentHistory.get().enrollments();
        assertNotNull(enrollments);
        assertEquals(6, enrollments.size());
    }

    @Test
    void empty_ifResponseDocumentHasNoContent() throws MagdaClientException {
        var enrollmentHistory = enrollmentHistory("00000000097");

        assertTrue(enrollmentHistory.isEmpty());
    }

    private Optional<EnrollmentHistory> enrollmentHistory(String insz) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefHistoriekInschrijvingRequest.builder()
                .insz(insz)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .build());

        return EnrollmentHistory.ofMagdaDocument(response.getDocument());
    }
}