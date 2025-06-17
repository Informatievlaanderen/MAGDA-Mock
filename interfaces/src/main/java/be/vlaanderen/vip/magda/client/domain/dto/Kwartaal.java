package be.vlaanderen.vip.magda.client.domain.dto;

import java.time.LocalDate;

public record Kwartaal(
        Integer jaar,
        Integer kwartaalcijfer
) {

    public Kwartaal verify() {
        if (kwartaalcijfer < 1 || kwartaalcijfer > 4) {
            throw new IllegalArgumentException("Kwartaalcijfer must be 1, 2, 3 or 4");
        }
        return this;
    }

    public String toString() {
        return String.format("%d-Q%d", jaar, kwartaalcijfer);
    }

    public static Kwartaal ofDate(LocalDate date){
        int kwartaal = switch (date.getMonth()) {
            case JANUARY, FEBRUARY, MARCH -> 1;
            case APRIL, MAY, JUNE -> 2;
            case JULY, AUGUST, SEPTEMBER -> 3;
            case OCTOBER, NOVEMBER, DECEMBER -> 4;
        };
        return new Kwartaal(date.getYear(), kwartaal);
    }
}
