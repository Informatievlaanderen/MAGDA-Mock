package be.vlaanderen.vip.mock.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefJaarrekeningenRequest;
import be.vlaanderen.vip.magda.client.domain.giveannualaccounts.AnnualAccounts;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MagdaResponseAnnualAccountsIntegrationTest {

    @Test
    void dataIsBoundToResponseDocument() throws MagdaClientException {
        var annualAccounts = annualAccounts("0200068636", Year.of(2017));

        assertNotNull(annualAccounts);
        var items = annualAccounts.annualAccounts();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    private AnnualAccounts annualAccounts(String kboNumber, Year financialYear) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefJaarrekeningenRequest.builder()
                .kboNumber(kboNumber)
                .financialYear(financialYear)
                .build());

        return AnnualAccounts.ofMagdaDocument(response.getDocument());
    }
}