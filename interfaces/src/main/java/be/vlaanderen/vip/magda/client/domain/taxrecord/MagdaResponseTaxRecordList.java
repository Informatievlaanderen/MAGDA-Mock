package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.xml.node.Node;

import java.util.List;

public record MagdaResponseTaxRecordList(MagdaResponseWrapper response) implements TaxRecordList {

    @Override
    public List<TaxRecord> getItems() {
        return response.getNodes("//AanslagbiljetPersonenbelasting/Items/Item")
                .map(this::toTaxRecord)
                .toList();
    }

    private TaxRecord toTaxRecord(Node itemMode) {
        return new TaxRecord(
                itemMode.get("Code")
                        .flatMap(Node::getValue)
                        .orElse(""),
                itemMode.get("Waarde")
                        .flatMap(Node::getValue)
                        .map(Double::parseDouble)
                        .orElse(0.0));
    }
}
