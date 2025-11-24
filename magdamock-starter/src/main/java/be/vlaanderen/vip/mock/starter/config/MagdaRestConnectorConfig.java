package be.vlaanderen.vip.mock.starter.config;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.mock.starter.connector.MagdaConnectorBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Data
@Configuration
@ConfigurationProperties(prefix = "magda.rest-mock-connector")
public class MagdaRestConnectorConfig {
    private boolean enabled;

    public MagdaConnector connector() {
        try {
            if (enabled) {
                return mockConnector();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create MagdaConnector", e);
        }
    }

    private MagdaConnector mockConnector() throws URISyntaxException, IOException {
        return MagdaConnectorBuilder.builder()
                .mockRest()
                .build();
    }
}
