package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.client.xml.XmlUtils;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import brave.Tracing;
import brave.httpclient5.HttpClient5TracingBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.w3c.dom.Document;

import javax.net.ssl.SSLContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Slf4j
public class MagdaSoapConnection implements MagdaConnection, Closeable {
    private final MagdaEndpoints magdaEndpoints;
    private final CloseableHttpClient httpClient;

    private MagdaSoapConnection(MagdaEndpoints magdaEndpoints, CloseableHttpClient httpClient) {
        this.magdaEndpoints = magdaEndpoints;
        this.httpClient = httpClient;
    }

    public MagdaSoapConnection(MagdaEndpoints magdaEndpoints, MagdaConfigDto config) throws TwoWaySslException {
        this(magdaEndpoints, buildHttpClient(buildHttpClientBuilder(buildSslConnectionFactoryFromConfig(config.getKeystore()))));

    }

    /**
     * Use this constructor in the subclasses to define own extensions on the HttpClient via the HttpClientBuilder.
     */
    protected MagdaSoapConnection(MagdaEndpoints magdaEndpoints, TwoWaySslProperties config, UnaryOperator<HttpClientBuilder> extraOptions) throws TwoWaySslException {
        this(magdaEndpoints, buildHttpClient(buildHttpClientBuilder(buildSslConnectionFactoryFromConfig(config)), extraOptions));
    }

    /**
     * @deprecated Use {@link MagdaBraveTracingSoapConnection}
     */
    @Deprecated(forRemoval = true)
    public MagdaSoapConnection(MagdaEndpoints magdaEndpoints, MagdaConfigDto config, Tracing tracing) throws TwoWaySslException {
        this(magdaEndpoints, buildHttpClient(buildHttpClientBuilder(buildSslConnectionFactoryFromConfig(config.getKeystore())), httpBuilder -> HttpClient5TracingBuilder.newBuilder(tracing).addTracer(httpBuilder)));
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

    private static CloseableHttpClient buildHttpClient(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
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

    @Override
    public Document sendDocument(Document xml) throws MagdaConnectionException {
        var doc = MagdaDocument.fromDocument(xml);
        var name = doc.getValue("//Verzoek/Context/Naam");
        var version = doc.getValue("//Verzoek/Context/Versie");

        var serviceId = new MagdaServiceIdentification(name, version);
        final var urlString = magdaEndpoints.magdaUri(serviceId).toString();

        log.info("Call to SOAP endpoint {}", urlString);
        final var request = new HttpPost(urlString);
        request.setHeader("Content-type", "text/xml;charset=UTF-8");
        request.setHeader("SOAPAction", "\"\"");

        try {
            request.setEntity(makeEntityWithXmlDocument(xml));
        } catch (TransformerException e) {
            throw new MagdaConnectionException(String.format("POST %s could not stream XML document", urlString), e);
        }

        try (var response = httpClient.execute(request)) {
            log.info("Response from SOAP endpoint {}: {}", urlString, response.getCode());

            if (response.getCode() == 200) {
                var responseEntity = response.getEntity();

                return parseStream(responseEntity.getContent());
            } else {
                var errorBody = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                log.error("POST {} failed with HTTP error {} {} and body {}", urlString, response.getCode(), response.getReasonPhrase(), errorBody);

                var exceptionMessage = String.format("POST %s faalt met HTTP error %d %s", urlString, response.getCode(), response.getReasonPhrase());
                throw new MagdaConnectionException(exceptionMessage, response.getCode());
            }
        } catch (IOException e) {
            throw new MagdaConnectionException(String.format("POST %s failed", urlString), e);
        }
    }

    private InputStreamEntity makeEntityWithXmlDocument(Document xml) throws TransformerException {
        var outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xml);
        Result outputTarget = new StreamResult(outputStream);
        XmlUtils.createTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        return new InputStreamEntity(is, ContentType.create("application/xml", StandardCharsets.UTF_8));
    }

    private Document parseStream(InputStream resource) {
        return MagdaDocument.fromStream(resource).getXml();
    }
}
