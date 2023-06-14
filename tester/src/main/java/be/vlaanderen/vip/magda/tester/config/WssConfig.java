package be.vlaanderen.vip.magda.tester.config;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("tester.wss")
public class WssConfig {

    private String keyStore;
    private String keyAlias;
    private String keyPassword;
    private String keystorePassword;

    public boolean isWssEnabled() {
        return getKeyStore() != null && !getKeyStore().equals("");
    }

    public TwoWaySslProperties getKeystoreProperties() {
        var keystore = new TwoWaySslProperties();
        keystore.setKeyStoreLocation(getKeyStore());
        keystore.setKeyAlias(getKeyAlias());
        keystore.setKeyPassword(getKeyPassword());
        keystore.setKeyStorePassword(getKeystorePassword());

        return keystore;
    }
}
