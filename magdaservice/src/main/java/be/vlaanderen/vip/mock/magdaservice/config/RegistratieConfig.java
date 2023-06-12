package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
