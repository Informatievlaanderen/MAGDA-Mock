package be.vlaanderen.vip.magda.client;

public interface MagdaClient {

    MagdaResponseWrapper send(MagdaRequest request) throws MagdaClientException;
    
}
