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
        if (StringUtils.isEmpty(certPath)) {
            System.err.println("Vul registratie.certPath configuratie in!");
        }
    }

}
