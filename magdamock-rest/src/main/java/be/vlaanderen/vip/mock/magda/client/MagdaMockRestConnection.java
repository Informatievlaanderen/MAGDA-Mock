package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.client.utils.MockDataTemplating;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockRestException;
import be.vlaanderen.vip.mock.magda.config.EmbeddedWireMockBuilder;
import be.vlaanderen.vip.mock.magda.config.MockRestMagdaEndpoints;
import be.vlaanderen.vip.mock.magda.config.WireMockData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Cookie;
import com.github.tomakehurst.wiremock.http.FormParameter;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.ImmutableRequest;
import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
            String dateHeader = request.getHeaders().get("Date");
            return sendRestRequest(path, queryParams, method, "", dateHeader);
        } catch (URISyntaxException e) {
            throw new MagdaMockRestException("Error simulating REST call", e.getCause());
        }
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(String path, String query, String method, String requestBody, String dateHeader) {
        List<String> parts = new ArrayList<>();
        parts.add(wireMockServer.url(path));
        if (!query.isEmpty()) {
            parts.add(query);
        }
        String url = String.join("?", parts);
        try {
            Response response = internalWiremockHttpServer.stubRequest(createInternalWiremockRequest(url, method, dateHeader));
            if (response.getStatus() == 404) {
                return Pair.of(null, 404);
            }
            String responseBody = response.getBodyAsString();
            return Pair.of(mapper.readTree(responseBody), response.getStatus());
        } catch (IOException e) {
            throw new MagdaMockRestException("Error simulating REST call", e.getCause());
        }
    }

    // As there need to be certain parameters filled in to avoid wiremock throwing nullpointers while templating, we create the request ourselves
    private Request createInternalWiremockRequest(String url, String method, String dateHeader) {
        if (dateHeader == null) {
            dateHeader = "";
        }
        HttpHeaders httpHeaders = new HttpHeaders(new HttpHeader("Date", dateHeader));
        return new Request() {
            @Override
            public String getUrl() {
                return Urls.getPathAndQuery(url);
            }

            @Override
            public String getAbsoluteUrl() {
                return url;
            }

            @Override
            public RequestMethod getMethod() {
                return RequestMethod.fromString(method);
            }

            @Override
            public String getScheme() {
                return "";
            }

            @Override
            public String getHost() {
                return "";
            }

            @Override
            public int getPort() {
                return wireMockServer.port();
            }

            @Override
            public String getClientIp() {
                return "";
            }

            @Override
            public String getHeader(String key) {
                List<String> values = header(key).getValues();
                if (values.isEmpty()) {
                    return "";
                }
                return values.getFirst();
            }

            @Override
            public HttpHeader header(String key) {
                return getHeaders().getHeader(key);
            }

            @Override
            public ContentTypeHeader contentTypeHeader() {
                return new ContentTypeHeader("application/json");
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpHeaders;
            }

            @Override
            public boolean containsHeader(String key) {
                return !getHeader(key).isEmpty();
            }

            @Override
            public Set<String> getAllHeaderKeys() {
                return getHeaders().keys();
            }

            @Override
            public QueryParameter queryParameter(String key) {
                return null;
            }

            @Override
            public FormParameter formParameter(String key) {
                return null;
            }

            @Override
            public Map<String, FormParameter> formParameters() {
                return Map.of();
            }

            @Override
            public Map<String, Cookie> getCookies() {
                return Map.of();
            }

            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public String getBodyAsString() {
                return "";
            }

            @Override
            public String getBodyAsBase64() {
                return "";
            }

            @Override
            public boolean isMultipart() {
                return false;
            }

            @Override
            public Collection<Part> getParts() {
                return List.of();
            }

            @Override
            public Part getPart(String name) {
                return null;
            }

            @Override
            public boolean isBrowserProxyRequest() {
                return false;
            }

            @Override
            public Optional<Request> getOriginalRequest() {
                return Optional.empty();
            }

            @Override
            public String getProtocol() {
                return "";
            }
        };
    }
}
