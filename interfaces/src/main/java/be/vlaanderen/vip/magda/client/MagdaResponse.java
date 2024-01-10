package be.vlaanderen.vip.magda.client;


import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
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

    /**
     * Uitzondering entries in the response's reply, which pertain to the processing of the request, also referred to as "level 2" uitzondering entries.
     *
     * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MAGDA/pages/190939419/Overzicht+Uitzonderingen+Codes">...</a>
     */
    private List<UitzonderingEntry> uitzonderingEntries;

    /**
     * Uitzondering entries in the response's subject matter, which pertain to the response, also referred to as "level 3" uitzondering entries.
     *
     * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MAGDA/pages/190939419/Overzicht+Uitzonderingen+Codes">...</a>
     */
    private List<UitzonderingEntry> responseUitzonderingEntries;

    private final Node body;

    private final MagdaDocument document;

    private final boolean hasContents;

    private final Set<SubjectIdentificationNumber> subjects;

    public boolean isBodyFilledIn() {
        return uitzonderingEntries.isEmpty() && hasContents && body != null;
    }
}
