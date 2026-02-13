package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.mock.magda.config.WireMockData;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MagdaMockRestConnectionTest {
    @Test
    @SneakyThrows
    void rawRestCallWithoutQuery_givesResponse() {
        DirectCallHttpServerFactory factory = new DirectCallHttpServerFactory();
        WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .httpServerFactory(factory);
        WireMockServer wireMockServer = new WireMockServer(config);
        wireMockServer.stubFor(
                get(urlEqualTo("/text/url"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"content\": \"Hello world!\"}"))
        );
        var mockConnection = MagdaMockRestConnection.create(new WireMockData(wireMockServer, factory));
        var response = mockConnection.sendRestRequest("/text/url", "", "GET", "", "");
        assertEquals(200, response.getRight());
        assertEquals("Hello world!", response.getLeft().get("content").textValue());
    }

    @Test
    @SneakyThrows
    void rawRestCallWithQuery_givesResponse() {
        DirectCallHttpServerFactory factory = new DirectCallHttpServerFactory();
        WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .httpServerFactory(factory);
        WireMockServer wireMockServer = new WireMockServer(config);
        wireMockServer.stubFor(
                get(urlEqualTo("/text/url?query=test"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"content\": \"Hello world!\"}"))
        );
        var mockConnection = MagdaMockRestConnection.create(new WireMockData(wireMockServer, factory));
        var response = mockConnection.sendRestRequest("/text/url", "query=test", "GET", "", "");
        assertEquals(200, response.getRight());
        assertEquals("Hello world!", response.getLeft().get("content").textValue());
    }
}
