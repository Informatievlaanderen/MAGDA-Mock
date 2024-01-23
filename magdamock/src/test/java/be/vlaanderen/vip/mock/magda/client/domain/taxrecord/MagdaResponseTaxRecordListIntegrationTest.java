package be.vlaanderen.vip.mock.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.taxrecord.MagdaResponseTaxRecordList;
import be.vlaanderen.vip.magda.client.domain.taxrecord.TaxRecord;
import be.vlaanderen.vip.magda.client.domain.taxrecord.TaxRecordList;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MagdaResponseTaxRecordListIntegrationTest {

    private TaxRecordList taxRecords;

    @BeforeEach
    void setup() {
        taxRecords = taxRecordList("00610122309", Year.of(2021));
    }

    @Test
    void getItems_returnsContainedTaxRecords() {
        assertEquals(List.of(
                        new TaxRecord("A7270", 3758645),
                        new TaxRecord("A7555", 3758645)),
                taxRecords.getItems());
    }

    private TaxRecordList taxRecordList(String insz, Year incomeYear) {
        var response = MagdaMock.getInstance()
                .getTaxRecords(insz, incomeYear);
        return new MagdaResponseTaxRecordList(new MagdaResponseWrapper(response));
    }
}