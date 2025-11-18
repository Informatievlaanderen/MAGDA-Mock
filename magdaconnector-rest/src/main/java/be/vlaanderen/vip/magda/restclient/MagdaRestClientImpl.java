package be.vlaanderen.vip.magda.restclient;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import be.vlaanderen.vip.magda.client.rest.MagdaRestClient;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Slf4j
public class MagdaRestClientImpl implements MagdaRestClient, Closeable {
    private final MagdaEndpoints magdaEndpoints;
    private final CloseableHttpClient httpClient;

    protected MagdaRestClientImpl(MagdaEndpoints magdaEndpoints, CloseableHttpClient httpClient) {
        this.magdaEndpoints = magdaEndpoints;
        this.httpClient = httpClient;
    }

    /**
     * Use this constructor in the subclasses to define own extensions on the HttpClient via the HttpClientBuilder.
     */
    MagdaRestClientImpl(MagdaEndpoints magdaEndpoints, TwoWaySslProperties config, UnaryOperator<HttpClientBuilder> extraOptions) throws TwoWaySslException {
        this(magdaEndpoints, buildHttpClient(buildHttpClientBuilder(buildSslConnectionFactoryFromConfig(config)), extraOptions));
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    private static SSLConnectionSocketFactory buildSslConnectionFactoryFromConfig(TwoWaySslProperties config) throws TwoWaySslException {
        if (Optional.ofNullable(config)
                .map(TwoWaySslProperties::getKeyStoreLocation)
                .map(StringUtils::isNotBlank)
                .orElse(false)) {

            var keystore = getKeystore(config.getKeyStoreType(), config.getKeyStoreLocation(), config.getKeyStorePassword().toCharArray());
            var sslContext = sslContext(keystore, config.getKeyAlias(), config.getKeyPassword().toCharArray());
            return sslConnectionSocketFactory(sslContext);

        } else {
            return SSLConnectionSocketFactory.getSystemSocketFactory();
        }
    }

    private static HttpClientBuilder buildHttpClientBuilder(SSLConnectionSocketFactory sslConnectionSocketFactory) {
        var connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(15))
                .setSocketTimeout(Timeout.ofSeconds(30))
                .build();
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultConnectionConfig(connectionConfig)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager);
    }

    private static CloseableHttpClient buildHttpClient(HttpClientBuilder httpClientBuilder, UnaryOperator<HttpClientBuilder> extraOptions) {
        httpClientBuilder = extraOptions.apply(httpClientBuilder);
        return httpClientBuilder.build();
    }

    private static SSLConnectionSocketFactory sslConnectionSocketFactory(SSLContext sslContext) {
        return SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(new DefaultHostnameVerifier())
                .build();
    }

    private static SSLContext sslContext(KeyStore keyStore, String keyAlias, char[] keyPassword) throws TwoWaySslException {
        try {
            return new SSLContextBuilder()
                    .loadKeyMaterial(keyStore, keyPassword, (map, socket) -> keyAlias)
                    .build();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            throw new TwoWaySslException("Failed to load SSL context", ex);
        }
    }

    private static KeyStore getKeystore(String keyStoreType, String keyStoreLocation, char[] keyStorePassword) throws TwoWaySslException {
        try {
            final var keyStore = KeyStore.getInstance(keyStoreType);
            try (final InputStream in = new FileInputStream(keyStoreLocation)) {
                keyStore.load(in, keyStorePassword);
                return keyStore;
            }
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException ex) {
            throw new TwoWaySslException("Failed to load the key store", ex);
        }
    }

    public MagdaResponseJson sendRestRequest(MagdaRestRequest request) throws MagdaConnectionException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(magdaEndpoints.magdaUri(request.getDienst()));
        for (Map.Entry<String, String> header : request.getUrlQueryParams().entrySet()) {
            uriBuilder.addParameter(header.getKey(), header.getValue());
        }
        URI urlString = uriBuilder.build();

        HttpUriRequestBase httpRequest = new HttpUriRequestBase(request.getMethod().name(), urlString);
        httpRequest.setHeader("Content-Type", "application/json");
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            httpRequest.setHeader(header.getKey(), header.getValue());
        }

        try (var response = httpClient.execute(httpRequest)) {
            log.info("Response from SOAP endpoint {}: {}", urlString, response.getCode());

            if (response.getCode() == 200) {
                var responseEntity = response.getEntity();

                return parseStream(responseEntity.getContent());
            } else {
                var errorBody = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                log.error("REST {} {} failed with HTTP error {} {} and body {}", request.getMethod(), urlString, response.getCode(), response.getReasonPhrase(), errorBody);

                var exceptionMessage = String.format("REST %s %s faalt met HTTP error %d %s", request.getMethod(), urlString, response.getCode(), response.getReasonPhrase());
                throw new MagdaConnectionException(exceptionMessage, response.getCode());
            }
        } catch (IOException e) {
            throw new MagdaConnectionException(String.format("REST call with method %s to %s failed", request.getMethod(), urlString), e);
        }
    }

    private MagdaResponseJson parseStream(InputStream content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(content);
        return new MagdaResponseJson(json);
    }
}
