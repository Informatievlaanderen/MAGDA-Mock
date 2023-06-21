package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * An "Uitzondering" entry within an "Uitzonderingen" section in a MAGDA response.
 *
 * These are domain-specific, backend-side errors that may occur at various levels during the processing of a request.
 * They are included in the response document as a list of Uitzondering entities which contain information on what happened.
 * An "Uitzonderingen" section in a response XML is modelled in the interface as a List of UitzonderingEntries.
 */
@Data
@Builder
public class UitzonderingEntry {

    private String identification;
    private String origin;
    private String diagnosis;
    private UitzonderingType uitzonderingType;
    private List<AnnotatieField> annotatieFields;

    public String toString() {
        return String.format("%s %s-%s '%s'", uitzonderingType == null ? "null" : uitzonderingType.toString(), origin, identification, diagnosis);
    }
}
