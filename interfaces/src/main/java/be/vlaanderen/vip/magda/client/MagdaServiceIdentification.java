package be.vlaanderen.vip.magda.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * An identifier of a specific service provided by MAGDA, which comprises a name and a version.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class MagdaServiceIdentification {
    private String name;
    private String version;
}
