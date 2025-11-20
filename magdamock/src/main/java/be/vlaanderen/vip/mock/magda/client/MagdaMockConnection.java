package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.config.EmbeddedWireMockBuilder;
import be.vlaanderen.vip.mock.magda.config.MockRestMagdaEndpoints;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
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
public class MagdaMockConnection implements MagdaConnection {

    private final SOAPSimulator simulator;
    private final WireMockServer wireMockServer;
    private final ObjectMapper mapper;
    private Document defaultResponse = null;
    private final HttpClient httpClient;

    MagdaMockConnection(SOAPSimulator simulator, WireMockServer wireMockServer) {
        this.simulator = simulator;
        this.wireMockServer = wireMockServer;
        this.wireMockServer.start();
        httpClient = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public static MagdaConnection create() throws URISyntaxException, IOException {
        return create(SOAPSimulatorBuilder.builder(ResourceFinders.magdaSimulator())
                .magdaMockSimulator()
                .build(),
                EmbeddedWireMockBuilder.wireMockServer());
    }

    public static MagdaConnection create(SOAPSimulator simulator, WireMockServer wireMockServer) {
        return new MagdaMockConnection(simulator, wireMockServer);
    }

    @Override
    public Document sendDocument(Document xml) {
        log.info("Answering using MAGDA Mock");

        if (defaultResponse != null) {
            var answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        return send(xml);
    }

    @Override
    public JsonNode sendRestRequest(MagdaRestRequest request) throws MagdaConnectionException, URISyntaxException {
        String queryParams = request.getUrlQueryParams().entrySet().stream().map((kv) -> String.format("%s=%s", kv.getKey(), kv.getValue())).collect(Collectors.joining("&"));
        String method = request.getMethod().name();

        String stubUrl = "http://stub";
        MockRestMagdaEndpoints endpoints = new MockRestMagdaEndpoints(new URI(stubUrl));
        List<String> parts = new ArrayList<>(Arrays.stream(endpoints.magdaUri(request.getDienst()).toString().split(stubUrl)).toList());
        parts.removeFirst();
        String path = String.join(stubUrl, parts);
        return sendRestRequest(path, queryParams, method, "");
    }

    @Override
    public JsonNode sendRestRequest(String path, String query, String method, String requestBody) throws MagdaConnectionException {
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
                return null;
            }
            return mapper.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            throw new MagdaConnectionException("Rest call failed", e);
        }
    }

    private Document send(Document xml) {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
