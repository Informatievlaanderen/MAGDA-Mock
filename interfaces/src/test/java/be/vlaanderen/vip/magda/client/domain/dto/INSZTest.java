package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class INSZTest {

    @Test
    void constructor_neverThrowsExceptions() {
        assertDoesNotThrow(() -> new INSZ("46061400105"));
        assertDoesNotThrow(() -> new INSZ("460614"));
        assertDoesNotThrow(() -> new INSZ("46061400106"));
        assertDoesNotThrow(() -> new INSZ("total bogus"));
        assertDoesNotThrow(() -> new INSZ(null));
    }

    @Test
    void toString_returnsValue() {
        assertEquals("46061400105", new INSZ("46061400105").toString());
    }
}