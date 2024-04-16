package be.vlaanderen.vip.magda.client.domain.dto;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

// XXX javadocs
@EqualsAndHashCode
public class PartialDate { // XXX naming: PartialDate or IncompleteDate?

    @Nullable Integer year;
    @Nullable Integer month;
    @Nullable Integer dayOfMonth;

    public static PartialDate fromString(String value) {
        return null; // XXX
    }

    public PartialDate(
            @Nullable Integer year,
            @Nullable Integer month,
            @Nullable Integer dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public PartialDate withYear(int year) {
        return new PartialDate(year, month, dayOfMonth);
    }

    public PartialDate withMonth(int month) {
        return new PartialDate(year, month, dayOfMonth);
    }

    public PartialDate withDayOfMonth(int dayOfMonth) {
        return new PartialDate(year, month, dayOfMonth);
    }

    // XXX maybe limit the complexity of withDayOfMonth by adding a withLastDayOfMonth method?

    public boolean hasYear() {
        return year != null;
    }

    public boolean hasMonth() {
        return month != null;
    }

    public boolean hasDayOfMonth() {
        return dayOfMonth != null;
    }

    public boolean isComplete() {
        return hasYear() && hasMonth() && hasDayOfMonth();
    }

    @SuppressWarnings("all") // XXX @SuppressWarnings("boxing") doesn't work in IntelliJ for some reason
    public LocalDate toLocalDate() throws IncompletePartialDateException {
        if(isComplete()) {
            return LocalDate.of(year, month, dayOfMonth);
        } else {
            throw new IncompletePartialDateException();
        }
    }
}