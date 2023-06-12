package be.vlaanderen.vip.magda.tester.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
@Data
@ConfigurationProperties("tester")
public class TesterConfig {
    private String serviceScheme;
    private String serviceHost;
    private int servicePort;
    private Duration probeRetryMils;
    private int probeRetryMax;

    public URI getServiceUrl() {
        return URI.create(serviceScheme + "://" + serviceHost + ":" + servicePort);
    }
}
