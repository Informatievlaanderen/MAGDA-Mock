package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

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

class MagdaResponseEnterpriseFunctionsTest {

    private final MagdaResponseEnterpriseFunctionsAdapter adapter = new MagdaResponseEnterpriseFunctionsAdapterJaxbImpl();

    @Test
    void producesEnterpriseFunctionsFromResponseXml() throws IOException, MagdaClientException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/enterprisefunctions/sample.xml")))
                .build();

        var enterpriseFunctions = adapter.adapt(new MagdaResponseWrapper(magdaResponse));

        assertNotNull(enterpriseFunctions);
        var enterpriseFunctionList = enterpriseFunctions.enterpriseFunctions();
        assertNotNull(enterpriseFunctionList);
        assertEquals(2, enterpriseFunctionList.size());
        assertEquals("0427643504", enterpriseFunctionList.get(0).enterpriseNumber());
        assertEquals("2", enterpriseFunctionList.get(0).personOrEnterpriseCode());
        assertEquals("0427643504", enterpriseFunctionList.get(1).enterpriseNumber());
        assertEquals("2", enterpriseFunctionList.get(1).personOrEnterpriseCode());
    }

    @Test
    void producesEnterpriseFunctionsFromResponseXmlV2() throws IOException, MagdaClientException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/enterprisefunctions/sample.xml")))
                .build();

        var enterpriseFunctions = adapter.adapt(new MagdaResponseWrapper(magdaResponse));

        assertNotNull(enterpriseFunctions);
        var enterpriseFunctionList = enterpriseFunctions.enterpriseFunctionsV2();
        assertNotNull(enterpriseFunctionList);
        assertEquals(2, enterpriseFunctionList.size());

        var function1 = enterpriseFunctions.enterpriseFunctionsV2().get(0);
        var function2 = enterpriseFunctions.enterpriseFunctionsV2().get(1);

        assertEquals("12345678901", function1.insz());
        assertEquals("03", function1.source().codeValue());
        assertEquals("KBO", function1.source().descriptionValue());
        assertEquals("0427643504", function1.functionHolderOf());
        assertEquals("2", function1.entityType().codeValue());
        assertEquals("Vertegenwoordiging door onderneming", function1.entityType().descriptionValue());
        assertEquals("0476821316", function1.enterpriseNumber());
        assertEquals("001", function1.sequenceNumber());
        assertEquals("10002", function1.functionType().codeValue());
        assertEquals("Bestuurder", function1.functionType().descriptionValue());
        assertEquals("10002", function1.functionTypeCAC().codeValue());
        assertEquals("Bestuurder", function1.functionTypeCAC().descriptionValue());
        assertEquals(LocalDate.of(2002, 05, 04), function1.period().getStartDate());
        assertEquals(LocalDate.of(9999, 12, 31), function1.period().getEndDate());
        assertEquals(LocalDate.of(2020, 01, 01), function1.periodCAC().getStartDate());
        assertEquals(LocalDate.of(2022, 07, 15), function1.periodCAC().getEndDate());
        assertEquals("999", function1.exemptionStatus().codeValue());
        assertEquals("geen vrijstelling mogelijk", function1.exemptionStatus().descriptionValue());
        assertEquals("666", function1.endOfFunction().codeValue());
        assertEquals("geen goesting meer", function1.endOfFunction().descriptionValue());

        assertEquals("03", function2.source().codeValue());
        assertEquals("KBO", function2.source().descriptionValue());
        assertEquals("0427643504", function2.functionHolderOf());
        assertEquals("2", function2.entityType().codeValue());
        assertEquals("Vertegenwoordiging door onderneming", function2.entityType().descriptionValue());
        assertEquals("0476821316", function2.enterpriseNumber());
        assertEquals("001", function2.sequenceNumber());
        assertEquals("10007", function2.functionType().codeValue());
        assertEquals("Gedelegeerd bestuurder", function2.functionType().descriptionValue());
        assertEquals("10002", function2.functionTypeCAC().codeValue());
        assertEquals("Bestuurder", function2.functionTypeCAC().descriptionValue());
        assertEquals(LocalDate.of(2002, 05, 04), function2.period().getStartDate());
        assertEquals(LocalDate.of(9999, 12, 31), function2.period().getEndDate());
        assertEquals(LocalDate.of(2020, 01, 01), function2.periodCAC().getStartDate());
        assertEquals(LocalDate.of(2022, 12, 31), function2.periodCAC().getEndDate());
        assertEquals("999", function2.exemptionStatus().codeValue());
        assertEquals("geen vrijstelling mogelijk", function2.exemptionStatus().descriptionValue());
        assertEquals("001", function2.exemptionType().codeValue());
        assertEquals("Verworven rechten:actief bij de start van de reglementering", function2.exemptionType().descriptionValue());
    }

    @Test
    void throwsErrorWhenNoResponse() throws IOException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/enterprisefunctions/error.xml")))
                .build();

        try {
            adapter.adapt(new MagdaResponseWrapper(magdaResponse));
        }
        catch (MagdaClientException e) {
            assertEquals("Could not parse magda response", e.getMessage());
        }
    }
}