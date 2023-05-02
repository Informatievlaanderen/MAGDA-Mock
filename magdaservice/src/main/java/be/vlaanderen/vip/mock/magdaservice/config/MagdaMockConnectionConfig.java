package be.vlaanderen.vip.mock.magdaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import be.vlaanderen.vip.mock.magdaservice.exception.InitializationException;

@Configuration
public class MagdaMockConnectionConfig {

    @Bean
    public ResourceFinder resourceFinder() {
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
    public MagdaMockConnection magdaMockConnection(SOAPSimulator simulator) {
        return new MagdaMockConnection(simulator);
    }
    
}
