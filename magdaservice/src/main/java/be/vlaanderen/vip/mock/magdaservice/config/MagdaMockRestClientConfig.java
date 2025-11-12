package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.mock.magdaservice.services.MockRestClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("magdamock-rest")
public class MagdaMockRestClientConfig {
    @Bean
    public MockRestClient mockRestClient(WireMockServer wireMockServer) {
        return new MockRestClient(wireMockServer);
    }
}
