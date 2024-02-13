package be.vlaanderen.vip.magda.client.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * An INSZ number.
 *
 * @see <a href="https://overheid.vlaanderen.be/personeel/regelgeving/insz-nummer">Information on INSZ numbers</a>
 */
@EqualsAndHashCode
public class INSZ {
    private static final Pattern RRNR_PATTERN = Pattern.compile("\\d{11}");

    @Getter
    private final String value;

    public static INSZ of(String value) {
        validate(value);

        return new INSZ(value);
    }

    private static void validate(String value) {
        if(value == null || !RRNR_PATTERN.matcher(value).matches() || !passesModulo97Check(value)) {
            throw new IllegalArgumentException("Invalid RRNR: %s".formatted(value));
        }
    }

    private static boolean passesModulo97Check(String value) {
        var base = Long.valueOf(value.substring(0, 9));

        // Note: The validation of INSZ Numbers for people born after 2000 differs,
        // cfr. https://nl.wikipedia.org/wiki/Rijksregisternummer
        var baseAfter2000 = Long.valueOf("2" + value.substring(0, 9));

        var check = Long.valueOf(value.substring(9));

        return (base + check) % 97 == 0 || (baseAfter2000 + check) % 97 == 0;
    }

    private INSZ(@NotNull String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}