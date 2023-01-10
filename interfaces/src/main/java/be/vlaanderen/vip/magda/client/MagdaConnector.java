package be.vlaanderen.vip.magda.client;


public interface MagdaConnector {
    @Deprecated
    MagdaAntwoord send(Aanvraag aanvraag, MagdaDocument request);

    MagdaAntwoord send(Aanvraag aanvraag);
}
