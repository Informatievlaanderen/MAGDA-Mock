package be.vlaanderen.vip.magda.client.domain.model.shared;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.util.Optional;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String stringValue) {
        return Optional.ofNullable(stringValue)
                .map(LocalDate::parse)
                .orElse(null);
    }

    @Override
    public String marshal(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(LocalDate::toString)
                .orElse(null);
    }
}
