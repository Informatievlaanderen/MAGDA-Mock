package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import be.vlaanderen.vip.mock.magdaservice.exception.InitializationException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("magdamock")
public class MagdaMockConnectionConfig {
    private String mockTestcasePath;

    @Bean
    public ResourceFinder resourceFinder() {
        if(StringUtils.isNotBlank(mockTestcasePath)) {
            return ResourceFinders.combined(mockTestcasePath(mockTestcasePath),
                                            magdaSimulator());
        }
        return magdaSimulator();
    }
    
    private ResourceFinder mockTestcasePath(String path) {
        return ResourceFinders.directory(path);
    }
    
    private ResourceFinder magdaSimulator() {
        return ResourceFinders.magdaSimulator();
    }
    
    @Bean
    public SOAPSimulator simulator(ResourceFinder finder, RegistratieConfig config) {
        var properties = config.getKeystoreProperties();
        
        var builder = SOAPSimulatorBuilder.builder(finder)
                                          .magdaMockSimulator();

        if(properties != null) {
            try {
                builder = builder.requestVerifierProperties(properties);
            } catch (TwoWaySslException e) {
                throw new InitializationException("Failed to create request verifier from properties", e);
            }
            
            try {
                builder = builder.responseSignerProperties(properties);
            } catch (TwoWaySslException e) {
                throw new InitializationException("Failed to create response signer from properties", e);
            }
        }
        return builder.build();
    }
    
    @Bean
    public MagdaConnection magdaMockConnection(SOAPSimulator simulator) {
        return MagdaMockConnection.create(simulator);
    }
    
}
