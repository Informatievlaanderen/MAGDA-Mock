package be.vlaanderen.vip.mock.magdaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "magdamock")
@Data
public class MagdaMockConfig {
    private Integer defaultresponsetime;

    private HashMap<String, Integer> responsetimes;

}
