package be.vlaanderen.vip.magda.client.domain.model.shared;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.Year;
import java.util.Optional;

public class YearXmlAdapter extends XmlAdapter<String, Year> {

    @Override
    public Year unmarshal(String stringValue) {
        return Optional.ofNullable(stringValue)
                .map(Year::parse)
                .orElse(null);
    }

    @Override
    public String marshal(Year year) {
        return Optional.ofNullable(year)
                .map(Year::toString)
                .orElse(null);
    }
}
