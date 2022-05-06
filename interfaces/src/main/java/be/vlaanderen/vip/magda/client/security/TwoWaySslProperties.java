package be.vlaanderen.vip.magda.client.security;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class TwoWaySslProperties {
    public static final String KEYSTORE_TYPE_JKS = "jks";
    public static final String KEYSTORE_TYPE_JWKS = "jwk";
    private String keyStoreType = "jks";
    private String keyStoreLocation;
    private String keyStorePassword;
    private String keyAlias;
    private String keyPassword;

    // In most cases the key password is the same as the keystore password, so just allow a bit of convenience here
    public String getKeyPassword() {
        if (StringUtils.isEmpty(keyPassword)) {
            return keyStorePassword;
        }
        return keyPassword;
    }
}
