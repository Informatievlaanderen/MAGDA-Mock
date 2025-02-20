package be.vlaanderen.vip.magda.client.domain.model.shared;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.Month;
import java.util.Optional;

public class MonthXmlAdapter extends XmlAdapter<String, Month> {

    @Override
    public Month unmarshal(String stringValue) {
        return Optional.ofNullable(stringValue)
                .map(Integer::parseUnsignedInt)
                .map(Month::of)
                .orElse(null);
    }

    @Override
    public String marshal(Month month) {
        return Optional.ofNullable(month)
                .map(Month::getValue)
                .map(Object::toString)
                .orElse(null);
    }
}
