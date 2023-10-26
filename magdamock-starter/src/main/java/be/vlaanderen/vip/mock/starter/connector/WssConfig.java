package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import lombok.Data;

@Data
public class WssConfig {
    private String keyStorePath;
    private String keyStorePassword;
    private String keyAlias;
    private String keyPassword;
    
    public TwoWaySslProperties getTwoWaySslProperties() {
        var properties = new TwoWaySslProperties();
        properties.setKeyStoreLocation(keyStorePath);
        properties.setKeyAlias(keyAlias);
        properties.setKeyPassword(keyPassword);
        properties.setKeyStorePassword(keyStorePassword);
        return properties;
    }
    
}
