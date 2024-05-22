package be.vlaanderen.vip.magda.client.domain.shared;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class OffsetDateTimeXmlAdapter extends XmlAdapter<String, OffsetDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public OffsetDateTime unmarshal(String stringValue) {
        return Optional.ofNullable(stringValue)
                .map(val -> DATE_TIME_FORMATTER.parse(val, LocalDateTime::from))
                .map(localDateTime -> OffsetDateTime.of(
                        localDateTime,
                        ZoneOffset.UTC))
                .orElse(null);
    }

    @Override
    public String marshal(OffsetDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(DATE_TIME_FORMATTER::format)
                .orElse(null);
    }
}