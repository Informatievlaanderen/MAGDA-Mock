package be.vlaanderen.vip.magda.restclient;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoint;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaRestClientImplTest {
    private MagdaRestClientImpl magdaRestClientImpl;

    @BeforeEach
    void setUp() {
        magdaRestClientImpl = null;
    }

    @Test
    @SneakyThrows
    public void testAllOkMocked() {
        MagdaEndpoints endpoints = mock(MagdaEndpoints.class);
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        magdaRestClientImpl = new MagdaRestTestClientImpl(endpoints, httpClient);

        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        MagdaRestRequest request = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .urlQueryParams(Map.of())
                .headers(Map.of())
                .build();
        when(endpoints.magdaUri(dienst)).thenReturn(URI.create("http://localhost"));
        when(httpClient.execute(any())).thenReturn(response);
        when(response.getCode()).thenReturn(200);
        String input = """
                {
                    "a": "b",
                    "c": 1,
                    "d": ["a", 1, {"y": "x"}]
                }
                """;
        when(response.getEntity()).thenReturn(new BasicHttpEntity(IOUtils.toInputStream(input, Charset.defaultCharset()), ContentType.APPLICATION_JSON));

        MagdaResponseJson magdaResponseJson = magdaRestClientImpl.sendRestRequest(request);
        Assertions.assertNotNull(magdaResponseJson);
        JsonNode json = magdaResponseJson.json();
        JSONAssert.assertEquals(input, json.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    public void testWithWiremockConnection() {
        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        MagdaRestRequest request = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .urlQueryParams(
                        Map.of("plateNr", "1XNN230"))
                .headers(Map.of())
                .build();

        String input = """
                {
                    "a": "b",
                    "c": 1,
                    "d": ["a", 1, {"y": "x"}]
                }
                """;
        WireMockServer wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/v1/mobility/registrations?plateNr=1XNN230"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(input)
                ));
        MagdaEndpoints endpoints = MagdaEndpoints.builder()
                .addMapping(dienst.getName(), dienst.getVersion(), MagdaEndpoint.of(wireMockServer.baseUrl() + "/v1/mobility/registrations"))
                .build();
        magdaRestClientImpl = new MagdaRestClientBuilder().withEndpoints(endpoints).build();
        MagdaResponseJson magdaResponseJson = magdaRestClientImpl.sendRestRequest(request);
        JsonNode json = magdaResponseJson.json();
        JSONAssert.assertEquals(input, json.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    public void testResponseStatus4xx() {
        MagdaEndpoints endpoints = mock(MagdaEndpoints.class);
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        magdaRestClientImpl = new MagdaRestTestClientImpl(endpoints, httpClient);

        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        MagdaRestRequest request = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .urlQueryParams(Map.of())
                .headers(Map.of())
                .build();
        when(endpoints.magdaUri(dienst)).thenReturn(URI.create("http://localhost"));
        when(httpClient.execute(any())).thenReturn(response);
        when(response.getCode()).thenReturn(418);
        String input = """
                {
                    "a": "b",
                    "c": 1,
                    "d": ["a", 1, {"y": "x"}]
                }
                """;
        when(response.getEntity()).thenReturn(new BasicHttpEntity(IOUtils.toInputStream(input, Charset.defaultCharset()), ContentType.APPLICATION_JSON));

        assertThrows(MagdaConnectionException.class, () -> magdaRestClientImpl.sendRestRequest(request));
    }

    @Test
    @SneakyThrows
    public void testEndpointNotFound() {
        MagdaEndpoints endpoints = mock(MagdaEndpoints.class);
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        magdaRestClientImpl = new MagdaRestTestClientImpl(endpoints, httpClient);

        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        MagdaRestRequest request = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .urlQueryParams(Map.of())
                .headers(Map.of())
                .build();
        when(endpoints.magdaUri(dienst)).thenReturn(URI.create("http://localhost"));
        when(httpClient.execute(any())).thenReturn(response);
        when(response.getCode()).thenReturn(200);
        String input = """
                {
                    "a": "b",
                    "c": 1,
                    "d": ["a", 1, {"y": "x"}]
                }
                """;
        when(response.getEntity()).thenReturn(new BasicHttpEntity(IOUtils.toInputStream(input, Charset.defaultCharset()), ContentType.APPLICATION_JSON));

        MagdaResponseJson magdaResponseJson = magdaRestClientImpl.sendRestRequest(request);
        Assertions.assertNotNull(magdaResponseJson);
        JsonNode json = magdaResponseJson.json();
        JSONAssert.assertEquals(input, json.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    public void testJsonInvalid() {
        MagdaEndpoints endpoints = mock(MagdaEndpoints.class);
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        magdaRestClientImpl = new MagdaRestTestClientImpl(endpoints, httpClient);

        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        MagdaRestRequest request = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .urlQueryParams(Map.of())
                .headers(Map.of())
                .build();
        when(endpoints.magdaUri(dienst)).thenReturn(URI.create("http://localhost"));
        when(httpClient.execute(any())).thenReturn(response);
        when(response.getCode()).thenReturn(200);
        String input = """
                {
                    "a": "b",
                    "c": 1,
                    "d": ["a", 1, {"y": "x"}]
                """;
        when(response.getEntity()).thenReturn(new BasicHttpEntity(IOUtils.toInputStream(input, Charset.defaultCharset()), ContentType.APPLICATION_JSON));

        assertThrows(MagdaConnectionException.class, () -> magdaRestClientImpl.sendRestRequest(request));
    }

    static class MagdaRestTestClientImpl extends MagdaRestClientImpl {
        public MagdaRestTestClientImpl(MagdaEndpoints magdaEndpoints, CloseableHttpClient httpClient) {
            super(magdaEndpoints, httpClient);
        }
    }

}
