package be.vlaanderen.vip.magda.client;


public interface MagdaConnector {

    MagdaResponse send(MagdaRequest magdaRequest);
}
