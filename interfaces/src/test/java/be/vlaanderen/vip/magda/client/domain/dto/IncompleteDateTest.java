package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static be.vlaanderen.vip.magda.client.domain.dto.IncompleteDate.CompletenessType.*;
import static org.junit.jupiter.api.Assertions.*;

class IncompleteDateTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "2024-06-15",
            "2024-01-31",
            "2023-02-28",
            "2024-02-29",
            "2024-04-30",
            "0000-00-31",
            "2024-00-00",
            "0000-00-00"})
    void fromString_constructsIncompleteDateFromString(String stringValue) {
        assertNotNull(IncompleteDate.fromString(stringValue));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2024-13-15",
            "2024-01-32",
            "2023-02-29",
            "2024-02-30",
            "2024-04-31",
            "0000-00-32",
            "bogus"})
    void fromString_whenStringIsInvalid_throwsException(String stringValue) {
        assertThrows(IllegalArgumentException.class, () -> IncompleteDate.fromString(stringValue));
    }

    @Test
    void equals_whenSameIncompleteDate() {
        assertEquals(
                IncompleteDate.fromString("2024-06-00"),
                IncompleteDate.fromString("2024-06-00"));
    }

    @Test
    void notEquals_whenDifferentIncompleteDate() {
        assertNotEquals(
                IncompleteDate.fromString("2024-06-15"),
                IncompleteDate.fromString("2024-06-00"));
    }

    @Test
    void hashCode_whenEquals_isSame() {
        assertEquals(
                IncompleteDate.fromString("2024-06-00").hashCode(),
                IncompleteDate.fromString("2024-06-00").hashCode());
    }

    @Test
    void hashCode_whenNotEquals_isDifferent() {
        assertNotEquals(
                IncompleteDate.fromString("2024-06-15").hashCode(),
                IncompleteDate.fromString("2024-06-00").hashCode());
    }

    @Test
    void year_whenYearIsGiven_returnsYearAsInteger() {
        assertEquals(2024, IncompleteDate.fromString("2024-06-00").year());
    }

    @Test
    void year_whenYearIsNotGiven_throwsException() {
        var date = IncompleteDate.fromString("0000-06-15");

        assertThrows(IncompleteDateMissingPartException.class, date::year);
    }

    @Test
    void month_whenMonthIsGiven_returnsMonthAsInteger() {
        assertEquals(6, IncompleteDate.fromString("2024-06-00").month());
    }

    @Test
    void month_whenMonthIsNotGiven_throwsException() {
        var date = IncompleteDate.fromString("2024-00-15");

        assertThrows(IncompleteDateMissingPartException.class, date::month);
    }

    @Test
    void dayOfMonth_whenDayOfMonthIsGiven_returnsDayOfMonthAsInteger() {
        assertEquals(15, IncompleteDate.fromString("2024-00-15").dayOfMonth());
    }

    @Test
    void dayOfMonth_whenDayOfMonthIsNotGiven_throwsException() {
        var date = IncompleteDate.fromString("2024-06-00");

        assertThrows(IncompleteDateMissingPartException.class, date::dayOfMonth);
    }

    private static Stream<Arguments> completenessType_isBasedOnWhichPartsAreAndAreNotGiven_parameters() {
        return Stream.of(
                Arguments.of("0000-00-00", NOTHING_GIVEN),
                Arguments.of("0000-00-15", YEAR_AND_MONTH_NOT_GIVEN),
                Arguments.of("0000-06-00", YEAR_AND_DAY_NOT_GIVEN),
                Arguments.of("0000-06-15", YEAR_NOT_GIVEN),
                Arguments.of("2024-00-00", MONTH_AND_DAY_NOT_GIVEN),
                Arguments.of("2024-00-15", MONTH_NOT_GIVEN),
                Arguments.of("2024-06-00", DAY_NOT_GIVEN),
                Arguments.of("2024-06-15", COMPLETE)

        );
    }

    @ParameterizedTest
    @MethodSource("completenessType_isBasedOnWhichPartsAreAndAreNotGiven_parameters")
    void completenessType_isBasedOnWhichPartsAreAndAreNotGiven(String stringValue, IncompleteDate.CompletenessType completenessType) {
        assertEquals(completenessType, IncompleteDate.fromString(stringValue).completenessType());
    }

    @Test
    void toLocalDate_whenIncompleteDateIsComplete_extractsLocalDate() {
        assertEquals(
                LocalDate.of(2024, 6, 15),
                IncompleteDate.fromString("2024-06-15").toLocalDate());
    }

    @Test
    void toLocalDate_whenIncompleteDateMissesAnyParts_throwsException() {
        assertThrows(IncompleteDateMissingPartException.class, () -> IncompleteDate.fromString("2024-06-00").toLocalDate());
    }

    @ParameterizedTest
    @MethodSource("toTextualRepresentation_parameters")
    void toTextualRepresentation(IncompleteDate incompleteDate, String result) {
        assertEquals(result, incompleteDate.toTextualRepresentation());
    }
    static Stream<Arguments> toTextualRepresentation_parameters() {
        return Stream.of(
                Arguments.of(IncompleteDate.fromString("0000-00-00"), "0000-00-00"),
                Arguments.of(IncompleteDate.fromString("1974-00-00"),  "1974-00-00"),
                Arguments.of(IncompleteDate.fromString("1974-06-00"), "1974-06-00"),
                Arguments.of(IncompleteDate.fromString("1974-00-12"), "1974-00-12")
        );
    }
}