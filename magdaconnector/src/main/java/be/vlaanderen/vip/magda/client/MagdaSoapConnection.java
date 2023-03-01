package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslUtil;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.util.Timeout;
import org.w3c.dom.Document;

import javax.net.ssl.SSLContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;

@Slf4j
public class MagdaSoapConnection implements MagdaConnection {
    private final MagdaEndpoints magdaEndpoints;
    private final CloseableHttpClient httpClient;

    public MagdaSoapConnection(MagdaEndpoints magdaEndpoints, MagdaConfigDto config) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        this.magdaEndpoints = magdaEndpoints;
        this.httpClient = buildHttpClient(buildSslConnectionFactoryFromConfig(config));
    }

    private static SSLConnectionSocketFactory buildSslConnectionFactoryFromConfig(MagdaConfigDto config) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        if (StringUtils.isNotEmpty(config.getKeystore().getKeyStoreLocation())) {

            KeyStore keystore = TwoWaySslUtil.getKeystore(config.getKeystore().getKeyStoreType(), config.getKeystore().getKeyStoreLocation(), config.getKeystore().getKeyStorePassword().toCharArray());
            SSLContext sslContext = TwoWaySslUtil.sslContext(keystore, config.getKeystore().getKeyAlias(), config.getKeystore().getKeyPassword().toCharArray());
            return TwoWaySslUtil.sslConnectionSocketFactory(sslContext);

        } else {
            return SSLConnectionSocketFactory.getSystemSocketFactory();
        }
    }

    private static CloseableHttpClient buildHttpClient(SSLConnectionSocketFactory sslConnectionSocketFactory) {
        var connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(15))
                .setSocketTimeout(Timeout.ofSeconds(30))
                .build();
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultConnectionConfig(connectionConfig)
                .build();
        var httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return httpClient;
    }

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        return sendDocument(xml);
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        var doc = MagdaDocument.fromDocument(xml) ;
        var service = doc.getValue("//Verzoek/Context/Naam");
        var versie = doc.getValue("//Verzoek/Context/Versie");

        var aanvraag = new MagdaServiceIdentificatie(service,versie) ;
        final String url = magdaEndpoints.magdaUrl(aanvraag);

        final HttpPost request = new HttpPost(url);
        request.setHeader("Content-type", "text/xml;charset=UTF-8");
        request.setHeader("SOAPAction", "\"\"");

        try {
            request.setEntity(makeEntityWithXmlDocument(xml));
        } catch (TransformerException e) {
            throw new MagdaSendFailed(String.format("POST %s kan request XML document niet streamen", url), e);
        }

        try(var response = httpClient.execute(request)) {
            if (response.getCode() == 200) {
                HttpEntity responseEntity = response.getEntity();
                return parseStream(responseEntity.getContent());
            } else {
                String errorBody = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                log.error("POST {} failed with HTTP error {} {} and body {}", url, response.getCode(), response.getReasonPhrase(), errorBody);

                String exceptionMessage = String.format("POST %s faalt met HTTP error %d %s", url, response.getCode(), response.getReasonPhrase());
                throw new MagdaSendFailed(exceptionMessage, response.getCode());
            }
        } catch (IOException e) {
            throw new MagdaSendFailed(String.format("POST %s gefaald", url), e);
        }
    }

    private InputStreamEntity makeEntityWithXmlDocument(Document xml) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xml);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        return new InputStreamEntity(is, ContentType.create("application/xml", "UTF-8"));
    }

    private Document parseStream(InputStream resource) {
        return MagdaDocument.fromStream(resource).getXml();
    }


}
