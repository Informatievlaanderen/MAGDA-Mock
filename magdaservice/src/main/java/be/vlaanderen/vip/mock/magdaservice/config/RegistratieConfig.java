package be.vlaanderen.vip.mock.magdaservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Getter
@Configuration
@ConfigurationProperties(value = "registratie")
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistratieConfig {
    private String hoedanigheid;
    private String identificatie;
    private String service;
    private String certPath;
    private String keyAlias;
    private String keyPassword;
    private String keystorePassword;

    @PostConstruct
    private void assertValues() {
        if (StringUtils.isEmpty(hoedanigheid)) {
            System.err.println("Vul registratie.hoedanigheid configuratie in!");
        }
        if (StringUtils.isEmpty(identificatie)) {
            System.err.println("Vul registratie.identificatie configuratie in!");
        }
        if (StringUtils.isEmpty(service)) {
            System.err.println("Vul registratie.service configuratie in!");
        }
        if (!StringUtils.isEmpty(certPath) && StringUtils.isEmpty(keyAlias)) {
            System.err.println("Vul registratie.keyAlias configuratie in!");
        }
        if (!StringUtils.isEmpty(certPath) && StringUtils.isEmpty(keyPassword)) {
            System.err.println("Vul registratie.keyPassword configuratie in!");
        }
        if (!StringUtils.isEmpty(certPath) && StringUtils.isEmpty(keystorePassword)) {
            System.err.println("Vul registratie.keystorePassword configuratie in!");
        }
    }
}
