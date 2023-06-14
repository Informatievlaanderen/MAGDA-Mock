package be.vlaanderen.vip.magda;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;

/*
 * For more information on how these are created, see /scripts/make_mock_jks.sh
 */
public class TestKeyStores {

    private TestKeyStores() {}

    public static final TwoWaySslProperties mockKeystoreProperties = new TwoWaySslProperties();
    static {
        mockKeystoreProperties.setKeyStoreLocation("src/test/resources/certificates/mock keystore.jks");
        mockKeystoreProperties.setKeyStorePassword("mijnpaswoord");
        mockKeystoreProperties.setKeyAlias("mock x509 certificaat");
        mockKeystoreProperties.setKeyPassword("mijnpaswoord");
    }

    public static final TwoWaySslProperties mockKeystorePropertiesAlt = new TwoWaySslProperties();
    static {
        mockKeystorePropertiesAlt.setKeyStoreLocation("src/test/resources/certificates/alternate mock keystore.jks");
        mockKeystorePropertiesAlt.setKeyStorePassword("mijnpaswoord2");
        mockKeystorePropertiesAlt.setKeyAlias("alternate mock x509 certificate");
        mockKeystorePropertiesAlt.setKeyPassword("mijnpaswoord2");
    }

    public static final TwoWaySslProperties mockKeystorePropertiesWithWrongPassword = new TwoWaySslProperties();
    static {
        mockKeystorePropertiesWithWrongPassword.setKeyStoreLocation("src/test/resources/certificates/mock keystore.jks");
        mockKeystorePropertiesWithWrongPassword.setKeyStorePassword("oeps");
        mockKeystorePropertiesWithWrongPassword.setKeyAlias("mock x509 certificaat");
        mockKeystorePropertiesWithWrongPassword.setKeyPassword("oeps");
    }
}
