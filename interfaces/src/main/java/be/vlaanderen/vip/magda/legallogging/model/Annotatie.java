package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;

@Builder
public record Annotatie(String naam, String waarde) { }
