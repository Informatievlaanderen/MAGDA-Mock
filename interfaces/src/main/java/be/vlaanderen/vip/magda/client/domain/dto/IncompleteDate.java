package be.vlaanderen.vip.magda.client.domain.dto;

import lombok.EqualsAndHashCode;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static be.vlaanderen.vip.magda.client.domain.dto.IncompleteDate.CompletenessType.COMPLETE;

/**
 * Information on a date of which some parts may not be given (for instance, a birthdate that is not exactly known).
 */
@EqualsAndHashCode
public class IncompleteDate {

    /**
     * The manner in which a date is incomplete.
     * The proper handling of incomplete date values in one's domain logic will depend on this.
     */
    public enum CompletenessType {

        /**
         * The date is entirely blank (ex: 0000-00-00).
         */
        NOTHING_GIVEN,

        /**
         * The date lacks both a year and a month (ex: 0000-00-15).
         */
        YEAR_AND_MONTH_NOT_GIVEN,

        /**
         * The date lacks both a year and a day of month (ex: 0000-06-00).
         */
        YEAR_AND_DAY_NOT_GIVEN,

        /**
         * The date lacks a year (ex: 0000-06-15).
         */
        YEAR_NOT_GIVEN,

        /**
         * The date lacks both a month and a day of month (ex: 2024-00-00).
         */
        MONTH_AND_DAY_NOT_GIVEN,

        /**
         * The date lacks a month (ex: 2024-00-15).
         */
        MONTH_NOT_GIVEN,

        /**
         * The date lacks a day of month (ex: 2024-06-00).
         */
        DAY_NOT_GIVEN,

        /**
         * The date is complete (ex: 2024-06-15).
         */
        COMPLETE
    }

    public static final Pattern INCOMPLETE_DATE_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    private final int year;
    private final int month;
    private final int dayOfMonth;

    public static IncompleteDate fromString(String value) {
        var matcher = INCOMPLETE_DATE_PATTERN.matcher(value);

        if(matcher.matches()) {
            var year = Integer.parseInt(matcher.group(1));
            var month = Integer.parseInt(matcher.group(2));
            var dayOfMonth = Integer.parseInt(matcher.group(3));

            try {
                LocalDate.of(
                        year == 0 ? 1 : year,
                        month == 0 ? 1 : month,
                        dayOfMonth == 0 ? 1 : dayOfMonth);
            } catch(DateTimeException ex) {
                throw new IllegalArgumentException("Invalid incomplete date: %s".formatted(value), ex);
            }

            return new IncompleteDate(year, month, dayOfMonth);
        } else {
            throw new IllegalArgumentException("Could not parse incomplete date value: %s".formatted(value));
        }
    }

    public IncompleteDate(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Get the information on the year part.
     *
     * @return The year as an integer.
     * @throws IncompleteDateMissingPartException if the year part is not given.
     */
    public int year() {
        if(year > 0) {
            return year;
        } else {
            throw new IncompleteDateMissingPartException("Year part is not given.");
        }
    }

    /**
     * Get the information on the month part.
     *
     * @return The month as an integer.
     * @throws IncompleteDateMissingPartException if the month part is not given.
     */
    public int month() {
        if(month > 0) {
            return month;
        } else {
            throw new IncompleteDateMissingPartException("Month part is not given.");
        }
    }

    /**
     * Get the information on the day of month part.
     *
     * @return The day of month as an integer.
     * @throws IncompleteDateMissingPartException if the day of month part is not given.
     */
    public int dayOfMonth() {
        if(dayOfMonth > 0) {
            return dayOfMonth;
        } else {
            throw new IncompleteDateMissingPartException("Day of month part is not given.");
        }
    }

    /**
     * Provides information on which parts are and are not given.
     * It's strongly recommended to branch on the completeness type prior to handling the incomplete date in any other way. Otherwise, {@link IncompleteDateMissingPartException}s cannot be ruled out.
     */
    public CompletenessType completenessType() {
        var idx = 0;
        if(year > 0) { idx += 4; }
        if(month > 0) { idx += 2; }
        if(dayOfMonth > 0) { idx += 1; }

        return CompletenessType.values()[idx];
    }

    /**
     * @return The date information as a LocalDate.
     * @throws IncompleteDateMissingPartException if the date information is not complete.
     */
    public LocalDate toLocalDate() throws IncompleteDateMissingPartException {
        if(completenessType() == COMPLETE) {
            return LocalDate.of(year, month, dayOfMonth);
        } else {
            throw new IncompleteDateMissingPartException("Date is incomplete.");
        }
    }
}