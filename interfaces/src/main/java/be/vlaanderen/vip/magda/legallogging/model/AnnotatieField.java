package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;

@Builder
public record AnnotatieField(String naam, String waarde) { }
