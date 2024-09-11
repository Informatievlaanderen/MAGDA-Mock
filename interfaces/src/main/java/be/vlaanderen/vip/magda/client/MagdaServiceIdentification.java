package be.vlaanderen.vip.magda.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * An identifier of a specific service provided by MAGDA, which comprises a name and a version.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class MagdaServiceIdentification implements Serializable {

    @Serial
    private static final long serialVersionUID = 5966457937074564979L;

    private String name;
    private String version;
}
