package be.vlaanderen.vip.magda.client.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OffsetDateTimeXmlAdapterTest {

    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC);
    private static final String OFFSET_DATE_TIME_STRING = "2024-01-02T03:04:05.000";

    private final OffsetDateTimeXmlAdapter adapter = new OffsetDateTimeXmlAdapter();

    @Test
    void unmarshal_parsesValue() {
        assertEquals(OFFSET_DATE_TIME, adapter.unmarshal(OFFSET_DATE_TIME_STRING));
    }

    @Test
    void unmarshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void marshal_formatsValue() {
        assertEquals(OFFSET_DATE_TIME_STRING, adapter.marshal(OFFSET_DATE_TIME));
    }

    @Test
    void marshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.marshal(null));
    }
}