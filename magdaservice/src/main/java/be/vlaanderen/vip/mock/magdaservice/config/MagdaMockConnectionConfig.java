package be.vlaanderen.vip.mock.magdaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magdaservice.exception.InitializationException;

@Configuration
public class MagdaMockConnectionConfig {

    @Bean
    public ResourceFinder resourceFinder() {
        return new ResourceFinder();
    }
    
    @Bean
    public MagdaMockConnection magdaMockConnection(ResourceFinder finder, RegistratieConfig config) {
        var keystore = config.getKeystoreProperties();
        try {
            return new MagdaMockConnection(finder, keystore, keystore);
        } catch (TwoWaySslException e) {
            throw new InitializationException(e);
        }
    }
    
}
