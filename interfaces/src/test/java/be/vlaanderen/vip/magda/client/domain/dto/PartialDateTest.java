package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PartialDateTest {

    @ParameterizedTest
    @ValueSource(strings = { "2024-06-15", "2024-00-00", "0000-00-00" })
    void fromString_constructsPartialDateFromString(String stringValue) {
        assertNotNull(PartialDate.fromString(stringValue));
    }

    @ParameterizedTest
    @ValueSource(strings = { "2024-13-15", "2024-04-31", "bogus" })
    void fromString_whenStringIsMalformed_throwsException(String stringValue) {
        assertThrows(IllegalArgumentException.class, () -> PartialDate.fromString(stringValue));
    }

    @Test
    void equals_whenSamePartialDate_isTrue() {
        assertEquals(
                PartialDate.fromString("2024-06-00"),
                PartialDate.fromString("2024-06-00"));
    }

    @Test
    void equals_whenDifferentPartialDate_isTrue() {
        assertNotEquals(
                PartialDate.fromString("2024-06-15"),
                PartialDate.fromString("2024-06-00"));
    }

    @Test
    void hashCode_whenEquals_isSame() {
        assertEquals(
                PartialDate.fromString("2024-06-00").hashCode(),
                PartialDate.fromString("2024-06-00").hashCode());
    }

    @Test
    void hashCode_whenNotEquals_isDifferent() {
        assertNotEquals(
                PartialDate.fromString("2024-06-15").hashCode(),
                PartialDate.fromString("2024-06-00").hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = { "0000-06-15", "2024-06-15" })
    void withYear_returnsNewPartialDateWithGivenYear(String originalStringValue) {
        var originalDate = PartialDate.fromString(originalStringValue);
        var newDate = originalDate.withYear(2024);

        assertAll(
                () -> assertEquals(originalDate, PartialDate.fromString(originalStringValue)),
                () -> assertEquals(newDate, PartialDate.fromString("2024-06-15"))
        );
    }

    @Test
    void withYear_whenValueInValidRange_doesNotThrowException() {
        var date = PartialDate.fromString("0000-00-00");

        assertDoesNotThrow(() -> date.withYear(0));
    }

    @Test
    void withYear_whenValueNotInValidRange_throwsException() {
        var date = PartialDate.fromString("0000-00-00");

        assertThrows(IllegalArgumentException.class, () -> date.withYear(-1));
    }

    @ParameterizedTest
    @ValueSource(strings = { "2024-00-15", "2024-06-15" })
    void withMonth_returnsNewPartialDateWithGivenMonth(String originalStringValue) {
        var originalDate = PartialDate.fromString(originalStringValue);
        var newDate = originalDate.withMonth(6);

        assertAll(
                () -> assertEquals(originalDate, PartialDate.fromString(originalStringValue)),
                () -> assertEquals(newDate, PartialDate.fromString("2024-06-15"))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 12 })
    void withMonth_whenValueInValidRange_doesNotThrowException(int month) {
        var date = PartialDate.fromString("0000-00-00");

        assertDoesNotThrow(() -> date.withMonth(month));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0, 13 })
    void withMonth_whenValueNotInValidRange_throwsException(int month) {
        var date = PartialDate.fromString("0000-00-00");

        assertThrows(IllegalArgumentException.class, () -> date.withMonth(month));
    }

    @Test
    void withMonth_whenDayWouldGetOutOfRangeUponGivingShorterMonth_throwsException() {
        var date = PartialDate.fromString("2023-01-31");
        var leapYear29thDate = PartialDate.fromString("2024-01-29");
        var nonLeapYear29thDate = PartialDate.fromString("2023-01-29");

        assertAll(
                () -> assertDoesNotThrow(() -> date.withMonth(3)),
                () -> assertDoesNotThrow(() -> leapYear29thDate.withMonth(2)),
                () -> assertThrows(IllegalArgumentException.class, () -> date.withMonth(2)),
                () -> assertThrows(IllegalArgumentException.class, () -> date.withMonth(4)),
                () -> assertThrows(IllegalArgumentException.class, () -> nonLeapYear29thDate.withMonth(2))
        );
    }

    // XXX shouldn't be able to set month when day is given, but not year

    @ParameterizedTest
    @ValueSource(strings = { "2024-06-00", "2024-06-15" })
    void withDayOfMonth_returnsNewPartialDateWithGivenDayOfMonth(String originalStringValue) {
        var originalDate = PartialDate.fromString(originalStringValue);
        var newDate = originalDate.withDayOfMonth(15);

        assertAll(
                () -> assertEquals(originalDate, PartialDate.fromString(originalStringValue)),
                () -> assertEquals(newDate, PartialDate.fromString("2024-06-15"))
        );
    }

    private static Stream<Arguments> withDayOfMonth_whenValueNotInValidRange_throwsException_parameters() {
        return Stream.of(
                Arguments.of(2023, 1, -1),
                Arguments.of(2023, 1, 0),
                Arguments.of(2023, 1, 32),
                Arguments.of(2023, 2, 29),
                Arguments.of(2024, 2, 30),
                Arguments.of(2023, 4, 31)
        );
    }

    @ParameterizedTest
    @MethodSource("withDayOfMonth_whenValueNotInValidRange_throwsException_parameters")
    void withDayOfMonth_whenValueNotInValidRange_throwsException(int year, int month, int dayOfMonth) {
        var date = PartialDate.fromString("0000-00-00").withYear(year).withMonth(month);

        assertThrows(IllegalArgumentException.class, () -> date.withDayOfMonth(dayOfMonth));
    }

    @Test
    void withDayOfMonth_whenMonthIsNotGiven_validRangeExtendsUpToDay28() {
        var date = PartialDate.fromString("2024-00-00");

        assertAll(
                () -> assertDoesNotThrow(() -> date.withDayOfMonth(28)),
                () -> assertThrows(IllegalArgumentException.class, () -> date.withDayOfMonth(29))
        );
    }

    @Test
    void hasYear_whenYearPartIsGiven_isTrue() {
        assertTrue(PartialDate.fromString("2024-06-00").hasYear());
    }

    @Test
    void hasYear_whenYearPartIsGiven_isFalse() {
        assertFalse(PartialDate.fromString("0000-06-00").hasYear());
    }

    @Test
    void hasMonth_whenMonthPartIsGiven_isTrue() {
        assertTrue(PartialDate.fromString("2024-06-00").hasMonth());
    }

    @Test
    void hasMonth_whenMonthPartIsGiven_isFalse() {
        assertFalse(PartialDate.fromString("2024-00-00").hasMonth());
    }

    @Test
    void hasDayOfMonth_whenDayOfMonthPartIsGiven_isTrue() {
        assertTrue(PartialDate.fromString("0000-06-15").hasDayOfMonth());
    }

    @Test
    void hasDayOfMonth_whenDayOfMonthPartIsGiven_isFalse() {
        assertFalse(PartialDate.fromString("0000-06-00").hasDayOfMonth());
    }

    private static Stream<Arguments> isComplete_onlyWhenAllPartsAreGiven_parameters() {
        return Stream.of(
                Arguments.of("2024-06-15", true),
                Arguments.of("2024-06-00", false),
                Arguments.of("2024-00-15", false),
                Arguments.of("0000-06-15", false),
                Arguments.of("0000-00-00", false)
        );
    }

    @ParameterizedTest
    @MethodSource("isComplete_onlyWhenAllPartsAreGiven_parameters")
    void isComplete_onlyWhenAllPartsAreGiven(String stringValue, boolean isComplete) {
        assertEquals(isComplete, PartialDate.fromString(stringValue).isComplete());
    }

    @Test
    void toLocalDate_whenPartialDateIsComplete_extractsLocalDate() {
        assertEquals(
                LocalDate.of(2024, 6, 15),
                PartialDate.fromString("2024-06-15").toLocalDate());
    }

    @Test
    void toLocalDate_whenPartialDateIsIncomplete_throwsException() {
        assertThrows(IncompletePartialDateException.class, () -> PartialDate.fromString("2024-06-00").toLocalDate());
    }
}