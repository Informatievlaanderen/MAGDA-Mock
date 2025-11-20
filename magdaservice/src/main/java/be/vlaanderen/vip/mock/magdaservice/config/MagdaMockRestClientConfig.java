package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.mock.magda.client.MagdaMockRestConnection;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("magdamock-rest")
public class MagdaMockRestClientConfig {
    @Bean("magdaRestConnection")
    public MagdaConnection magdaMockRestConnection() {
        return MagdaMockRestConnection.create();
    }
}
