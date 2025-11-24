package be.vlaanderen.vip.mock.starter;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.mock.starter.config.MagdaConnectorConfig;
import be.vlaanderen.vip.mock.starter.config.MagdaRestConnectorConfig;
import brave.Tracing;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultTemplate;
import org.zalando.logbook.Logbook;

@AutoConfiguration(after = {VaultAutoConfiguration.class})
@EnableConfigurationProperties({MagdaConnectorConfig.class, MagdaRestConnectorConfig.class})
public class MagdaConnectorAutoConfiguration {

    @Bean("magdaConnector")
    @ConditionalOnMissingBean
    public MagdaConnector magdaConnector(
            MagdaConnectorConfig config,
            @Nullable VaultTemplate template,
            @Nullable Logbook logbook,
            @Nullable Tracing tracing,
            @Nullable ObservationRegistry observationRegistry) {
        return config.connector(template, logbook, tracing, observationRegistry);
    }

    @Bean("magdaRestConnector")
    @ConditionalOnProperty("magda.rest-mock-connector.enabled")
    public MagdaConnector magdaRestConnector(
            MagdaRestConnectorConfig config) {
        return config.connector();
    }
}
