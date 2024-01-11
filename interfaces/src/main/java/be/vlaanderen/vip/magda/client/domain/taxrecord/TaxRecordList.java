package be.vlaanderen.vip.magda.client.domain.taxrecord;

import java.util.List;

/**
 * Information on a citizen's tax records.
 */
public interface TaxRecordList {

    /**
     * List of the tax records.
     */
    List<TaxRecord> getItems();
}