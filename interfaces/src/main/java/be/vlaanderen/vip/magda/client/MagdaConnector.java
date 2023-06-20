package be.vlaanderen.vip.magda.client;


public interface MagdaConnector {

    MagdaAntwoord send(MagdaRequest magdaRequest);
}
