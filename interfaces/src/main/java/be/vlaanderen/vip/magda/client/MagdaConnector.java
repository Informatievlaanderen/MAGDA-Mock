package be.vlaanderen.vip.magda.client;

import java.util.concurrent.CompletableFuture;

public interface MagdaConnector {
    MagdaAntwoord send(Aanvraag aanvraag, MagdaDocument request);

    CompletableFuture<MagdaAntwoord> sendAsync(Aanvraag aanvraag, MagdaDocument request);
}
