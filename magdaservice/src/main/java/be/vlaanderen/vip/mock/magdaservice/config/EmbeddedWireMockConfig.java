package be.vlaanderen.vip.mock.magdaservice.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedWireMockConfig {

    @Value("${wiremock.port}")
    private int wiremockPort;

    @Value("${wiremock.mappings-path}")
    private String mappingsPath;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
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