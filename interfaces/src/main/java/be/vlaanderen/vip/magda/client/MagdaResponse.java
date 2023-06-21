package be.vlaanderen.vip.magda.client;


import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class MagdaResponse {
    private final UUID correlationId;
    private final UUID requestId;
    private List<UitzonderingEntry> uitzonderingen;
    private List<UitzonderingEntry> antwoordUitzonderingen;
    private final Node body;
    private final MagdaDocument document;
    private final boolean heeftInhoud;
    private final Set<String> insz;

    public boolean isBodyIngevuld() {
        return uitzonderingen.isEmpty() && heeftInhoud && body != null;
    }
}
