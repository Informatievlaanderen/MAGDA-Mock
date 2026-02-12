package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockRestException;
import be.vlaanderen.vip.mock.magda.config.EmbeddedWireMockBuilder;
import be.vlaanderen.vip.mock.magda.config.MockRestMagdaEndpoints;
import be.vlaanderen.vip.mock.magda.config.WireMockData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServer;
import com.github.tomakehurst.wiremock.http.ImmutableRequest;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MagdaMockRestConnection implements MagdaConnection {

    private final WireMockServer wireMockServer;
    private final ObjectMapper mapper;
    private final DirectCallHttpServer internalWiremockHttpServer;

    MagdaMockRestConnection(WireMockData wiremockServerData) {
        this.wireMockServer = wiremockServerData.wireMockServer();
        internalWiremockHttpServer = wiremockServerData.factory().getHttpServer();
        mapper = new ObjectMapper();
    }

    public static MagdaConnection create() {
        return create(EmbeddedWireMockBuilder.wireMockServer());
    }

    public static MagdaConnection create(WireMockData wiremockServerData) {
        return new MagdaMockRestConnection(wiremockServerData);
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
        List<String> parts = new ArrayList<>();
        parts.add(wireMockServer.url(path));
        if (!query.isEmpty()){
            parts.add(query);
        }
        String url = String.join("?", parts);
        try {
            Request mockRequest = new ImmutableRequest.Builder()
                    .withAbsoluteUrl(url)
                    .withMethod(RequestMethod.fromString(method))
                    .build();
            Response response = internalWiremockHttpServer.stubRequest(mockRequest);

            if (response.getStatus() == 404) {
                return Pair.of(null, 404);
            }
            return Pair.of(mapper.readTree(response.getBody()), response.getStatus());
        } catch (IOException e) {
            throw new MagdaMockRestException("Error simulating REST call", e.getCause());
        }
    }
}
