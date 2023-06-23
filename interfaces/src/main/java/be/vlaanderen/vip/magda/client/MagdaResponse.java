package be.vlaanderen.vip.magda.client;


import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The contents of a response from a MAGDA service.
 */
@Getter
@Builder
@AllArgsConstructor
public class MagdaResponse {
    private final UUID correlationId;
    private final UUID requestId;
    private List<UitzonderingEntry> uitzonderingEntries;
    private List<UitzonderingEntry> responseUitzonderingEntries;
    private final Node body;
    private final MagdaDocument document;
    private final boolean hasContents;
    private final Set<String> insz; // XXX -> subjects?

    public boolean isBodyIngevuld() {
        return uitzonderingEntries.isEmpty() && hasContents && body != null;
    }
}
