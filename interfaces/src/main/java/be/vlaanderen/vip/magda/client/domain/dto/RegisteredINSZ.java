package be.vlaanderen.vip.magda.client.domain.dto;

import java.io.Serializable;

/**
 * Marker record for an INSZ number (of some type) that has been registered in the MAGDA Repertorium.
 *
 * @see INSZ
 */
public record RegisteredINSZ(INSZ insz) implements Serializable {

    public String toString() {
        return insz.toString();
    }
}