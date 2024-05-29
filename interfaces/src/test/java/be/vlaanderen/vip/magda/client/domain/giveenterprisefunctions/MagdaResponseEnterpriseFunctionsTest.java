package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.TestHelpers;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        assertEquals("123", enterpriseFunctionList.get(0).enterpriseNumber());
        assertEquals("1", enterpriseFunctionList.get(0).personOrEnterpriseCode());
        assertEquals("456", enterpriseFunctionList.get(1).enterpriseNumber());
        assertEquals("2", enterpriseFunctionList.get(1).personOrEnterpriseCode());
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