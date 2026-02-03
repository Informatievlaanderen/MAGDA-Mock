package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockRestException;
import be.vlaanderen.vip.mock.magda.config.EmbeddedWireMockBuilder;
import be.vlaanderen.vip.mock.magda.config.MockRestMagdaEndpoints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MagdaMockRestConnection implements MagdaConnection {

    private final WireMockServer wireMockServer;
    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    MagdaMockRestConnection(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        this.wireMockServer.start();
        httpClient = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public static MagdaConnection create() {
        return create(EmbeddedWireMockBuilder.wireMockServer());
    }

    public static MagdaConnection create(WireMockServer wireMockServer) {
        return new MagdaMockRestConnection(wireMockServer);
    }

    @Override
    public Document sendDocument(Document xml) {
        throw new NotImplementedException();
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(MagdaRestRequest request) {
        String queryParams = request.getUrlQueryParams().entrySet().stream().map((kv) -> String.format("%s=%s", kv.getKey(), kv.getValue())).collect(Collectors.joining("&"));
        String method = request.getMethod().name();

        try {
            String stubUrl = "http://stub";
            MockRestMagdaEndpoints endpoints = new MockRestMagdaEndpoints(new URI(stubUrl));
            List<String> parts = new ArrayList<>(Arrays.stream(endpoints.magdaUri(request.getDienst()).toString().split(stubUrl)).toList());
            parts.removeFirst();
            String path = String.join(stubUrl, parts);
            return sendRestRequest(path, queryParams, method, "");
        } catch (URISyntaxException e) {
            throw new MagdaMockRestException("Error simulating REST call", e.getCause());
        }
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(String path, String query, String method, String requestBody) {
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
                return Pair.of(null, 404);
            }
            return Pair.of(mapper.readTree(response.body()), response.statusCode());
        } catch (IOException | InterruptedException e) {
            throw new MagdaMockRestException("Error simulating REST call", e.getCause());
        }
    }
}
