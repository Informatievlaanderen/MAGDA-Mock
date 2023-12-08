package be.vlaanderen.vip.mock.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultTemplate;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.mock.starter.config.MagdaConnectorConfig;
import brave.Tracing;

@AutoConfiguration(after = { VaultAutoConfiguration.class })
@EnableConfigurationProperties(MagdaConnectorConfig.class)
public class MagdaConnectorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MagdaConnector magdaConnector(
            MagdaConnectorConfig config,
            @Nullable VaultTemplate template,
            @Nullable Tracing tracing) {
        return config.connector(template, tracing);
    }
    
}
