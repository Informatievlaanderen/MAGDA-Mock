package be.vlaanderen.vip.magda.client.domain.dto;

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
}
