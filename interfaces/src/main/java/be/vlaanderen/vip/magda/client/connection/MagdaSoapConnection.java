package be.vlaanderen.vip.magda.client.connection;



import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Slf4j
public class MagdaSoapConnection implements MagdaConnection {
    private final MagdaEndpoints magdaEndpoints;
    private final SSLConnectionSocketFactory sslConnectionSocketFactory;

    public MagdaSoapConnection(MagdaEndpoints magdaEndpoints, MagdaConfigDto config) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        this.magdaEndpoints = magdaEndpoints;

        if (StringUtils.isNotEmpty(config.getKeystore().getKeyStoreLocation())) {
            KeyStore keystore = TwoWaySslUtil.getKeystore(config.getKeystore().getKeyStoreType(), config.getKeystore().getKeyStoreLocation(), config.getKeystore().getKeyStorePassword().toCharArray());
            SSLContext sslContext = TwoWaySslUtil.sslContext(keystore, config.getKeystore().getKeyAlias(), config.getKeystore().getKeyPassword().toCharArray());
            sslConnectionSocketFactory = TwoWaySslUtil.sslConnectionSocketFactory(sslContext);
        } else {
            sslConnectionSocketFactory = SSLConnectionSocketFactory.getSystemSocketFactory();
        }
    }

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml)  throws MagdaSendFailed {
        final String url = magdaEndpoints.magdaUrl(aanvraag.magdaService());

        final HttpPost request = new HttpPost(url);
        request.setHeader("Content-type", "text/xml;charset=UTF-8");
        request.setHeader("SOAPAction", "\"\"");

        try {
            request.setEntity(makeEntityWithXmlDocument(xml));
        } catch (TransformerException e) {
            throw new MagdaSendFailed(String.format("POST %s kan request XML document niet streamen", url), e);
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(15_000) // time to wait to get connection from the pool, since we always create a new httpclient this shouldn't be an issue
                .setConnectTimeout(15_000)
                .setSocketTimeout(30_000)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultRequestConfig(config)
                .build();

        Document answer;
        try {
            HttpResponse response = httpClient.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();
                answer = parseStream(responseEntity.getContent());
            } else {
                String errorBody = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                log.contextDetail(aanvraag.getRequestId().toString()).error("POST {} failed with HTTP error {} {} and body {}", url, statusLine.getStatusCode(), statusLine.getReasonPhrase(), errorBody);

                String exceptionMessage = String.format("POST %s faalt met HTTP error %d %s", url, statusLine.getStatusCode(), statusLine.getReasonPhrase());
                throw new MagdaSendFailed(exceptionMessage, statusLine.getStatusCode());
            }
        } catch (Exception e) {
            throw new MagdaSendFailed(String.format("POST %s gefaald", url), e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
            }
        }

        return answer;
    }

    private InputStreamEntity makeEntityWithXmlDocument(Document xml) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xml);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        return new InputStreamEntity(is, ContentType.create("application/xml", Consts.UTF_8));
    }

    private Document parseStream(InputStream resource) {
        return MagdaDocument.fromStream(resource).getXml();
    }


}
