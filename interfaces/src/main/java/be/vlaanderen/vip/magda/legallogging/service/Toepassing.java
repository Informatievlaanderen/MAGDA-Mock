package be.vlaanderen.vip.magda.legallogging.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Toepassing {
    private final String naam;
    private final String uri;
    private final String hoedanigheid;
}
