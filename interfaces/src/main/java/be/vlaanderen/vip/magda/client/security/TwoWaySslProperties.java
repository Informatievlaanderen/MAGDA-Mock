package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSConfig;

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

    public Crypto toCrypto() throws TwoWaySslException {
        WSSConfig.init();

        var props = new java.util.Properties();
        props.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", getKeyStoreType());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", getKeyStorePassword());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", getKeyAlias());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", getKeyStoreLocation());

        try {
            return CryptoFactory.getInstance(props);
        } catch (WSSecurityException ex) {
            throw new TwoWaySslException("Failed to create a WSS crypto", ex);
        }
    }
}
