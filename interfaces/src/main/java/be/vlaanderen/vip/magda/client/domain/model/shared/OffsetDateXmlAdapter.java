package be.vlaanderen.vip.magda.client.domain.model.shared;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class OffsetDateXmlAdapter extends XmlAdapter<String, OffsetDateTime> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public OffsetDateTime unmarshal(String stringValue) {
        return Optional.ofNullable(stringValue)
                .map(val -> DATE_FORMATTER.parse(stringValue, LocalDate::from))
                .map(localDate -> OffsetDateTime.of(
                        LocalDateTime.of(localDate, LocalTime.of(0, 0, 0, 0)),
                        ZoneOffset.UTC))
                .orElse(null);
    }

    @Override
    public String marshal(OffsetDateTime localDate) {
        return Optional.ofNullable(localDate)
                .map(DATE_FORMATTER::format)
                .orElse(null);
    }
}