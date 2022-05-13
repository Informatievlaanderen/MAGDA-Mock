package be.vlaanderen.vip.magda.client;


import be.vlaanderen.vip.magda.legallogging.model.Uitzondering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Getter
@Builder
@AllArgsConstructor
public class MagdaAntwoord {
    private final UUID correlationId;
    private final UUID requestId;
    private List<Uitzondering> uitzonderingen;
    private List<Uitzondering> antwoordUitzonderingen;
    private final Node body;
    private final MagdaDocument document;
    private final boolean heeftInhoud;
    private final Set<String> insz;

    public void verwijderUitzonderingen(Predicate<Uitzondering> reject) {
        uitzonderingen.removeIf(reject);
        antwoordUitzonderingen.removeIf(reject);
    }

    public boolean isBodyIngevuld() {
        return uitzonderingen.isEmpty() && heeftInhoud && body != null;
    }
}
