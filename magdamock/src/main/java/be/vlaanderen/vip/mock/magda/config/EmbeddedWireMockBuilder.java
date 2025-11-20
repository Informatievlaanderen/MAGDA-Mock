package be.vlaanderen.vip.mock.magda.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class EmbeddedWireMockBuilder {
    
    public static WireMockServer wireMockServer() {
        return wireMockServer(0, "classpath:/wiremock");
    }

    public static WireMockServer wireMockServer(Integer wiremockPort, String mappingsPath) {
        // Get the path relative to the classpath root
        String filesRoot = mappingsPath.replace("classpath:/", "");

        WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .port(wiremockPort)
                .usingFilesUnderClasspath(filesRoot);

        WireMockServer wireMockServer = new WireMockServer(config);

        System.out.println(
                "### Starting Embedded WireMockServer on port " + wiremockPort +
                        " with mappings from " + mappingsPath + " ###"
        );

        return wireMockServer;
    }
}