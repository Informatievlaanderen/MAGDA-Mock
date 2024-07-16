package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;

import java.util.List;

@FunctionalInterface
public interface Level3UitzonderingenSeverityCheck {

    boolean isSevere(List<UitzonderingEntry> uitzonderingEntries);
}