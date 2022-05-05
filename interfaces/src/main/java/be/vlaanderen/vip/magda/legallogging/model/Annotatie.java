package be.vlaanderen.vip.magda.legallogging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Annotatie {
    private final String naam;
    private final String waarde;
}
