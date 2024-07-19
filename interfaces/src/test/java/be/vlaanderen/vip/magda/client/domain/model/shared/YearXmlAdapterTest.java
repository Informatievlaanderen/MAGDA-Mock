package be.vlaanderen.vip.magda.client.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class YearXmlAdapterTest {

    private static final Year YEAR = Year.of(2024);
    private static final String YEAR_STRING = "2024";

    private final YearXmlAdapter adapter = new YearXmlAdapter();

    @Test
    void unmarshal_parsesValue() {
        assertEquals(YEAR, adapter.unmarshal(YEAR_STRING));
    }

    @Test
    void unmarshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void marshal_formatsValue() {
        assertEquals(YEAR_STRING, adapter.marshal(YEAR));
    }

    @Test
    void marshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.marshal(null));
    }
}