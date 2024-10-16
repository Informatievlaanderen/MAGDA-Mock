package be.vlaanderen.vip.mock.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefJaarrekeningenRequest;
import be.vlaanderen.vip.magda.client.domain.giveannualaccounts.AnnualAccounts;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static be.vlaanderen.vip.magda.client.domain.giveannualaccounts.AnnualAccounts.*;
import static org.junit.jupiter.api.Assertions.*;

class MagdaResponseAnnualAccountsIntegrationTest {

    private AnnualAccounts annualAccounts;
    private AnnualAccount annualAccount;

    @BeforeEach
    void setup() throws MagdaClientException {
        annualAccounts = annualAccounts("0200068636", Year.of(2017));
        assertNotNull(annualAccounts);
        var items = annualAccounts.annualAccounts();
        assertNotNull(items);
        assertEquals(1, items.size());

        annualAccount = items.getFirst();
        assertNotNull(annualAccount);
    }

    @Nested
    class Headers {

        private Header header;

        @BeforeEach
        void setup() {
            header = annualAccount.header();
            assertNotNull(header);
        }

        @Test
        void mapsFinancialYear() {
            var financialYear = header.financialYear();
            assertNotNull(financialYear);
            assertEquals(Year.of(2017), financialYear.year());
        }

        @Test
        void mapsSchema() {
            var schema = header.schema();
            assertNotNull(schema);
            assertEquals("02", schema.codeValue());
            assertEquals("Volledig model kapitaalvennootschap", schema.codeDescription());
        }

        @Test
        void mapsTypeSchema() {
            var typeSchema = header.typeSchema();
            assertNotNull(typeSchema);
            assertEquals("40", typeSchema.codeValue());
            assertEquals("Volledig model voor vennootschappen", typeSchema.descriptionValue());
        }

        @Test
        void mapsNatureCode() {
            var nature = header.nature();
            assertNull(nature); // XXX we seem to have no examples at all of documents that have this field?
        }
    }

    @Nested
    class Elements {

        private Element element;

        @BeforeEach
        void setup() {
            var elements = annualAccount.elements();
            assertNotNull(elements);
            assertEquals(373, elements.size());

            element = elements.getFirst();
            assertNotNull(element);
        }

        @Test
        void mapsRubric() {
            assertEquals("1003P", element.rubric());
        }

        @Test
        void mapsNumberAmount() {
            var numberAmount = element.numberAmount();
            assertNotNull(numberAmount);
            assertEquals("74890", numberAmount.value());
        }
    }

    private AnnualAccounts annualAccounts(String kboNumber, Year financialYear) throws MagdaClientException {
        var response = MagdaMock.getInstance().send(GeefJaarrekeningenRequest.builder()
                .kboNumber(kboNumber)
                .financialYear(financialYear)
                .build());

        return ofMagdaDocument(response.getDocument());
    }
}