package be.vlaanderen.vip.magda.client.security;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSConfig;

public class TwoWaySslUtil {

    private TwoWaySslUtil() {}

    public static Crypto constructCryptoFromProperties(TwoWaySslProperties twoWaySslProperties) throws TwoWaySslException {
        WSSConfig.init();

        var props = new java.util.Properties();
        props.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", twoWaySslProperties.getKeyStoreType());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", twoWaySslProperties.getKeyStorePassword());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", twoWaySslProperties.getKeyAlias());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", twoWaySslProperties.getKeyStoreLocation());

        try {
            return CryptoFactory.getInstance(props);
        } catch (WSSecurityException ex) {
            throw new TwoWaySslException("Failed to create a WSS crypto", ex);
        }
    }
}
