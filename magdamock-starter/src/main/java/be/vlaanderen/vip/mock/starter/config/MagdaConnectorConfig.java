package be.vlaanderen.vip.mock.starter.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import be.vlaanderen.vip.mock.starter.connector.*;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultTemplate;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
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
    private MagdaConnectorTracing tracing = MagdaConnectorTracing.BRAVE; // BRAVE by default for backward compatibility, should be removed in the future

    public MagdaConnector connector(VaultTemplate template,
                                    Tracing tracing,
                                    ObservationRegistry observationRegistry) {
        try {
            return switch (connection) {
                case Mock -> mockConnector();
                case Remote -> remoteConnector(template, tracing, observationRegistry);
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

    private MagdaConnector remoteConnector(VaultTemplate template,
                                           Tracing braveTracing,
                                           ObservationRegistry micrometerObservationRegistry) throws TwoWaySslException {
        switch (tracing) {
            case NONE -> {
                return remoteConnector(template);
            }
            case BRAVE -> {
                if (braveTracing == null) {
                    throw new IllegalStateException("Tracing is set to BRAVE but no Brave Tracing bean is available");
                }
                return remoteConnector(template, braveTracing);
            }
            case MICROMETER -> {
                if (micrometerObservationRegistry == null) {
                    throw new IllegalStateException("Tracing is set to MICROMETER but no Micrometer ObservationRegistry bean is available");
                }
                return remoteConnector(template, micrometerObservationRegistry);
            }
            case null, default -> {
                // backwards-compatibility: auto-sense if the Brave Tracing bean has been configured (if no tracing parameter is set)
                if (braveTracing == null) {
                    return remoteConnector(template);
                }
                return remoteConnector(template, braveTracing);
            }
        }
    }

    private MagdaConnector remoteConnector(VaultTemplate template) throws TwoWaySslException {
        return remoteConnectorBuilder(template).build();
    }

    private MagdaConnector remoteConnector(VaultTemplate template, Tracing tracing) throws TwoWaySslException {
        return remoteConnectorBuilder(template)
                .tracing(tracing)
                .build();
    }

    private MagdaConnector remoteConnector(VaultTemplate template, ObservationRegistry observationRegistry) throws TwoWaySslException {
        return remoteConnectorBuilder(template)
                .micrometer(observationRegistry)
                .build();
    }

    private RemoteMagdaConnectionBuilder remoteConnectorBuilder(VaultTemplate template) {
        return MagdaConnectorBuilder.builder()
                .remote()
                .magdaConfig(config(template)) // It's OK if this is null, wss config will be used then
                .magdaEndpoints(endpoints());
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
