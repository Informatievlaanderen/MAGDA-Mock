package be.vlaanderen.vip.magda.client.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OffsetDateXmlAdapterTest {

    private static final OffsetDateTime OFFSET_DATE = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final String OFFSET_DATE_STRING = "2024-01-02";

    private final OffsetDateXmlAdapter adapter = new OffsetDateXmlAdapter();

    @Test
    void unmarshal_parsesValue() {
        assertEquals(OFFSET_DATE, adapter.unmarshal(OFFSET_DATE_STRING));
    }

    @Test
    void unmarshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void marshal_formatsValue() {
        assertEquals(OFFSET_DATE_STRING, adapter.marshal(OFFSET_DATE));
    }

    @Test
    void marshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.marshal(null));
    }
}