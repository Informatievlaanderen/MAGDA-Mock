package be.vlaanderen.vip.magda.client.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static be.vlaanderen.vip.magda.client.utils.HashingUtils.hashSha512;

@Data
@Slf4j
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

    public void logHashes() {
        log.info("TwoWaySslProperties: keyStoreType {}", keyStoreType);
        log.info("TwoWaySslProperties: keyStoreLocation {}", keyStoreLocation);
        log.info("TwoWaySslProperties: keyStorePassword {}", hashSha512(keyStorePassword));
        log.info("TwoWaySslProperties: keyAlias {}", keyAlias);
        log.info("TwoWaySslProperties: keyPassword {}", hashSha512(keyPassword));
    }
}
