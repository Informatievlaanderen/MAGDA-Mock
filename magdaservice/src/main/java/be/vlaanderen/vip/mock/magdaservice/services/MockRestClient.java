package be.vlaanderen.vip.mock.magdaservice.services;

import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MockRestClient {
    private final WireMockServer wireMockServer;
    private final HttpClient httpClient;

    public MockRestClient(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        httpClient = HttpClient.newHttpClient();
    }

    public ResponseEntity<String> processRestRequest(String path, String query, String method, String requestBody) throws MagdaConnectionException {
        String url = wireMockServer.url(path) + "?" + query;
        try {
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(method, body)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(response.body(), HttpStatusCode.valueOf(response.statusCode()));
        } catch (IOException | InterruptedException e) {
            throw new MagdaConnectionException("Rest call failed", e);
        }
    }
}
