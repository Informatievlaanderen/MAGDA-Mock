package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;

public class Level3UitzonderingenSeverityChecks {

    public static final Level3UitzonderingenSeverityCheck SEVERE_ON_ERROR = uitzonderingEntries ->
            uitzonderingEntries.stream()
                    .anyMatch(uitzonderingEntry -> uitzonderingEntry.getUitzonderingType().equals(UitzonderingType.FOUT));

    private Level3UitzonderingenSeverityChecks() {}
}