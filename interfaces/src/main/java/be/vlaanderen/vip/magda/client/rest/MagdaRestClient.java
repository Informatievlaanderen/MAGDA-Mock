package be.vlaanderen.vip.magda.client.rest;

import be.vlaanderen.vip.magda.exception.MagdaConnectionException;

import java.net.URISyntaxException;

public interface MagdaRestClient {
    MagdaResponseJson sendRestRequest(MagdaRestRequest request) throws MagdaConnectionException, URISyntaxException;
}
