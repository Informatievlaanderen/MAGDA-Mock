package be.vlaanderen.vip.magda.client.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalDateXmlAdapterTest {

    private static final LocalDate LOCAL_DATE = LocalDate.of(2024, 1, 2);
    private static final String LOCAL_DATE_STRING = "2024-01-02";

    private final LocalDateXmlAdapter adapter = new LocalDateXmlAdapter();

    @Test
    void unmarshal_parsesValue() {
        assertEquals(LOCAL_DATE, adapter.unmarshal(LOCAL_DATE_STRING));
    }

    @Test
    void unmarshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void marshal_formatsValue() {
        assertEquals(LOCAL_DATE_STRING, adapter.marshal(LOCAL_DATE));
    }

    @Test
    void marshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.marshal(null));
    }
}