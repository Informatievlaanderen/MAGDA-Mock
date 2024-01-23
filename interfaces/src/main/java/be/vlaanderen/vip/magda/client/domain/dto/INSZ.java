package be.vlaanderen.vip.magda.client.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * An INSZ number.
 *
 * @see <a href="https://overheid.vlaanderen.be/personeel/regelgeving/insz-nummer">Information on INSZ numbers</a>
 */
@EqualsAndHashCode
public class INSZ {

    @Getter
    private final String value;

    public INSZ(@NotNull String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}