package be.vlaanderen.vip.mock.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefOndernemingRequest;
import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MagdaResponseEnterpriseIntegrationTest {

    private Enterprise enterprise;

    @BeforeEach
    void setup() throws MagdaClientException {
        enterprise = enterprise("0202239951");
        assertNotNull(enterprise);
    }

    @Test
    void mapsEnterpriseNumber() {
        assertEquals("0202239951", enterprise.enterpriseNumber());
    }

    @Test
    void mapsCompanyNames() {
        var companyNames = enterprise.companyNames();
        assertNotNull(companyNames);
        var registeredNames = companyNames.registeredNames();
        assertNotNull(registeredNames);
        assertEquals(2, registeredNames.size());
        var registeredName = registeredNames.getFirst();
        assertNotNull(registeredName);
        assertEquals("PROXIMUS", registeredName.name());
        assertEquals("fr", registeredName.languageCode());
    }

    @Test
    void mapsEnterpriseType() {
        var enterpriseType = enterprise.enterpriseType();
        assertNotNull(enterpriseType);
        assertEquals("2", enterpriseType.codeValue());
        assertEquals("Rechtspersoon", enterpriseType.codeDescription());
    }

    @Test
    void mapsLegalForms() {
        var legalForms = enterprise.legalForms();
        assertNotNull(legalForms);
        assertEquals(1, legalForms.size());
        var legalForm = legalForms.getFirst();
        assertNotNull(legalForm);
        assertEquals("114", legalForm.codeValue());
        assertEquals("Naamloze vennootschap van publiek recht", legalForm.descriptionValue());
    }

    @Test
    void mapsStatusKBO() {
        var statusKBO = enterprise.statusKBO();
        assertNotNull(statusKBO);
        assertEquals("AC", statusKBO.codeValue());
        assertEquals("Actief", statusKBO.descriptionValue());
    }

    @Test
    void mapsStartDate() {
        var startDate = enterprise.startDate();
        assertNotNull(startDate);
        assertEquals(LocalDate.of(1930, 7, 19), startDate.value());
    }

    @Test
    void returnsNullIfEnterpriseIsAbsent() throws MagdaClientException {
        assertNull(enterprise("0874713140"));
    }

    private Enterprise enterprise(String kboNumber) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefOndernemingRequest.builder()
                .kboNumber(kboNumber)
                .build());

        return Enterprise.ofMagdaDocument(response.getDocument());
    }
}