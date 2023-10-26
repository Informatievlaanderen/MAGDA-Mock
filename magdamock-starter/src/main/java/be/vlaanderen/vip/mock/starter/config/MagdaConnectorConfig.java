package be.vlaanderen.vip.mock.starter.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultTemplate;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import be.vlaanderen.vip.mock.starter.connector.ConnectionType;
import be.vlaanderen.vip.mock.starter.connector.MagdaConfigBuilder;
import be.vlaanderen.vip.mock.starter.connector.MagdaConnectorBuilder;
import be.vlaanderen.vip.mock.starter.connector.MagdaEndpointMapping;
import be.vlaanderen.vip.mock.starter.connector.MagdaRegistration;
import be.vlaanderen.vip.mock.starter.connector.VaultWssConfig;
import be.vlaanderen.vip.mock.starter.connector.WssConfig;
import brave.Tracing;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "magda.connector")
public class MagdaConnectorConfig {
    private ConnectionType connection;
    private VaultWssConfig vault;
    private WssConfig wss;
    private Map<String, MagdaRegistration> registrations = new HashMap<>();
    private List<MagdaEndpointMapping> endpoints = new ArrayList<>();
    
    public MagdaConnector connector(VaultTemplate template, Tracing tracing) {
        try {
            return switch(connection) {
                case Mock -> mockConnector();
                case Remote -> remoteConnector(template, tracing);
            };
        }
        catch(Exception e) {
            throw new IllegalStateException("Failed to create MagdaConnector", e);
        }
    }
    
    private MagdaConnector mockConnector() throws URISyntaxException, IOException {
        return MagdaConnectorBuilder.builder()
                                    .mock()
                                    .build();
    }
    
    private MagdaConnector remoteConnector(VaultTemplate template, Tracing tracing) throws TwoWaySslException {
        return MagdaConnectorBuilder.builder()
                                    .remote()
                                    .magdaConfig(config(template))
                                    .magdaEndpoints(endpoints())
                                    .tracing(tracing)
                                    .build();
    }
    
    private MagdaConfigDto config(VaultTemplate template) {
        return MagdaConfigBuilder.builder()
                                 .vaultWssConfig(template, vault)
                                 .wssConfig(wss)
                                 .registrations(registrations())
                                 .build();
    }
    
    private MagdaEndpoints endpoints() {
        return MagdaEndpointMapping.toMagdaEndpoints(endpoints);
    }
    
    private Map<String, MagdaRegistrationConfigDto> registrations() {
        return registrations.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Entry::getKey, 
                                                      e -> e.getValue().toMagdaRegistrationConfigDto()));
    }
}
