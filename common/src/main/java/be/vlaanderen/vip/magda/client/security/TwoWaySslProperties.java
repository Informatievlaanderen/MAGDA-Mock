package be.vlaanderen.vip.magda.client.security;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
        log.info("TwoWaySslProperties: keyStoreType {}", hashSha512(keyStoreType));
        log.info("TwoWaySslProperties: keyStoreLocation {}", hashSha512(keyStoreLocation));
        log.info("TwoWaySslProperties: keyStorePassword {}", hashSha512(keyStorePassword));
        log.info("TwoWaySslProperties: keyAlias {}", hashSha512(keyAlias));
        log.info("TwoWaySslProperties: keyPassword {}", hashSha512(keyPassword));
    }

    @SneakyThrows
    private String hashSha512(String s){
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(s.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder hashtext = new StringBuilder(no.toString(16));
        while (hashtext.length() < 128) {
            hashtext.insert(0, "0");
        }
        return hashtext.toString();
    }
}
