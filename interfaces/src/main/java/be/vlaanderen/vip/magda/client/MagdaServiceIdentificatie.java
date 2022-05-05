package be.vlaanderen.vip.magda.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class MagdaServiceIdentificatie {
    private String naam;
    private String versie;
}
