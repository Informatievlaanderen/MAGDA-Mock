package be.vlaanderen.vip.magda.client.security;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class TwoWaySslUtil {

    public static CloseableHttpClient twoWaySslHttpClient(SSLConnectionSocketFactory socketFactory) {
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(1000)
                .setMaxConnPerRoute(250)
                .setSSLSocketFactory(socketFactory)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    public static SSLConnectionSocketFactory sslConnectionSocketFactory(SSLContext sslContext) {
        return SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(new DefaultHostnameVerifier())
                .build();
    }

    public static SSLContext sslContext(KeyStore keyStore, char[] keyPassword) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new SSLContextBuilder()
                .loadKeyMaterial(keyStore, keyPassword)
                .build();
    }

    public static SSLContext sslContext(KeyStore keyStore, String keyAlias, char[] keyPassword) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new SSLContextBuilder()
                .loadKeyMaterial(keyStore, keyPassword, (map, socket) -> keyAlias)
                .build();
    }

    public static KeyStore getKeystore(final TwoWaySslProperties properties) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        return getKeystore(properties.getKeyStoreType(), properties.getKeyStoreLocation(), properties.getKeyStorePassword().toCharArray());
    }

    public static KeyStore getKeystore(String keyStoreType, String keyStoreLocation, char[] keyStorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try (final InputStream in = new FileInputStream(keyStoreLocation)) {
            keyStore.load(in, keyStorePassword);
            return keyStore;
        }
    }
}
