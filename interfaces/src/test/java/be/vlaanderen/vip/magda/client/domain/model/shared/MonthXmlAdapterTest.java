package be.vlaanderen.vip.magda.client.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MonthXmlAdapterTest {

    private static final Month MONTH = Month.MAY;
    private static final String MONTH_STRING = "5";

    private final MonthXmlAdapter adapter = new MonthXmlAdapter();

    @Test
    void unmarshal_parsesValue() {
        assertEquals(MONTH, adapter.unmarshal(MONTH_STRING));
    }

    @Test
    void unmarshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void marshal_formatsValue() {
        assertEquals(MONTH_STRING, adapter.marshal(MONTH));
    }

    @Test
    void marshal_yieldsNullWhenValueIsNull() {
        assertNull(adapter.marshal(null));
    }
}