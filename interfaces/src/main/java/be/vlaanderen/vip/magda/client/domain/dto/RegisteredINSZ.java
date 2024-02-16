package be.vlaanderen.vip.magda.client.domain.dto;

/**
 * Marker record for an INSZ number (of some type) that has been registered in the MAGDA Repertorium.
 *
 * @see INSZ
 */
public record RegisteredINSZ(INSZ insz) {

    public String toString() {
        return insz.toString();
    }
}