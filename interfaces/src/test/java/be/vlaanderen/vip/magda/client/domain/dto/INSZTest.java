package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class INSZTest {

    /*
    For more information on the validation, see:
    https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1127907704/Identificatienummer+Persoon-02.02#Extra-info
    */

    @Test
    void of_ifValueIsValidRRNRNumber_returnsRRNRWithGivenValue() {
        var rrnr = INSZ.of("46061400105");

        assertNotNull(rrnr);
        assertEquals("46061400105", rrnr.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"00012556777", "03060212051"})
    void of_ifValueAfter2000IsValidRRNRNumber_returnsRRNRWithGivenValue(String value) {
        var rrnr = INSZ.of(value);

        assertNotNull(rrnr);
        assertEquals(value, rrnr.getValue());
    }

    @Test
    void of_ifValueIsHighRRNRNumber_returnsRRNRWithGivenValue() {
        var rrnr = INSZ.of("99999999964");

        assertNotNull(rrnr);
        assertEquals("99999999964", rrnr.getValue());
    }

    @Test
    void of_throwsException_ifValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> INSZ.of(null));
    }

    @Test
    void of_throwsException_ifValueIsNotOfCorrectLength() {
        assertThrows(IllegalArgumentException.class, () -> INSZ.of("460614"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"460614E0105", "-46061400105", "46061400.105"})
    void of_throwsException_ifValueDoesNotConsistOfDigits(String value) {
        assertThrows(IllegalArgumentException.class, () -> INSZ.of(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"46061400106", "12345678901"})
    void of_throwsException_ifValueFailsModulo97Check(String value) {
        assertThrows(IllegalArgumentException.class, () -> INSZ.of(value));
    }
    @Test
    void toString_returnsValue() {
        assertEquals("46061400105", INSZ.of("46061400105").toString());
    }
}