package be.vlaanderen.vip.magda.client.security;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class TwoWaySslUtil {
    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public static RestTemplateBuilder twoWaySslRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder, TwoWaySslProperties properties) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keystore = getKeystore(properties);
        SSLContext sslContext = sslContext(keystore, properties.getKeyPassword().toCharArray());
        SSLConnectionSocketFactory socketFactory = sslConnectionSocketFactory(sslContext);
        HttpClient httpClient = twoWaySslHttpClient(socketFactory);

        // RequestFactory maken met de custom TwoWay SSL Client
        // Een BufferingRequestFactory maken zodat de inputstream opnieuw gelezen kan worden
        restTemplateBuilder = restTemplateBuilder.requestFactory(() -> new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient)));
        return restTemplateBuilder;
    }

    public static RestTemplateBuilder twoWaySslRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder, SSLContext sslContext) {
        SSLConnectionSocketFactory socketFactory = sslConnectionSocketFactory(sslContext);
        HttpClient httpClient = twoWaySslHttpClient(socketFactory);

        // RequestFactory maken met de custom TwoWay SSL Client
        // Een BufferingRequestFactory maken zodat de inputstream opnieuw gelezen kan worden
        restTemplateBuilder = restTemplateBuilder.requestFactory(() -> new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient)));
        return restTemplateBuilder;
    }

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
        final Resource resource = resourceLoader.getResource(keyStoreLocation);
        try (final InputStream in = resource.getInputStream()) {
            keyStore.load(in, keyStorePassword);
            return keyStore;
        }
    }

    public static PrivateKey loadKeyMaterial(
            final KeyStore keystore,
            final char[] keyPassword,
            final String alias)
            throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        final KeyManager[] kms = kmfactory.getKeyManagers();
        if (kms != null) {
            for (int i = 0; i < kms.length; i++) {
                final KeyManager km = kms[i];
                if (km instanceof X509ExtendedKeyManager) {
                    return ((X509ExtendedKeyManager) km).getPrivateKey(alias);
                }
            }

        }
        return null;
    }
}
