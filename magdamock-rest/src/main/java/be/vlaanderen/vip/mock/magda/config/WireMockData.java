package be.vlaanderen.vip.mock.magda.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory;
import org.apache.commons.lang3.tuple.Pair;

public record WireMockData(WireMockServer wireMockServer, DirectCallHttpServerFactory factory) {
}
