package be.vlaanderen.vip.mock.magdaservice.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(value = "registratie")
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
    
    public TwoWaySslProperties getKeystoreProperties() {
        log.debug("Reading key store from config");
        if(certPath != null) {
            var keystore = new TwoWaySslProperties();
            keystore.setKeyStoreLocation(certPath);
            keystore.setKeyStorePassword(keystorePassword);
            keystore.setKeyAlias(keyAlias);
            keystore.setKeyPassword(keystorePassword);
            return keystore;
        } else {
            log.warn("Key store was not configured");
            return null;
        }
    }
}
