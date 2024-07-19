package be.vlaanderen.vip.mock.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory.EnrollmentHistory;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MagdaResponseGiveEnrollmentHistoryIntegrationTest {

    private EnrollmentHistory enrollmentHistory;

    @BeforeEach
    void setup() throws MagdaClientException {
        enrollmentHistory = enrollmentHistory("88611099807");
    }

    @Test
    void dataIsBoundToResponseDocument() {
        var enrollments = enrollmentHistory.enrollments();
        assertNotNull(enrollments);
        assertEquals(6, enrollments.size());
    }

    private EnrollmentHistory enrollmentHistory(String insz) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefHistoriekInschrijvingRequest.builder()
                .insz(insz)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .build());

        return EnrollmentHistory.ofMagdaDocument(response.getDocument());
    }
}