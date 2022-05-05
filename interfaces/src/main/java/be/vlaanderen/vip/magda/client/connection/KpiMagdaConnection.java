package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.burgerprofiel.wwoom.kpi.ApiLog;
import be.vlaanderen.burgerprofiel.wwoom.kpi.KpiLogs;
import be.vlaanderen.burgerprofiel.wwoom.kpi.ManualRecorder;
import be.vlaanderen.burgerprofiel.wwoom.kpi.registry.ApiDecoration;
import be.vlaanderen.burgerprofiel.wwoom.kpi.registry.ApiRegistry;
import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@RequiredArgsConstructor
public class KpiMagdaConnection implements MagdaConnection {

    private final MagdaEndpoints magdaEndpoints;
    private final MagdaConnection monitoredConnection;
    private final KpiLogs kpiLogs;
    private final ApiRegistry apiRegistry;

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        if (canMonitorCalls()) {
            return monitoredCall(aanvraag, xml);
        }
        return normalCall(aanvraag, xml);
    }

    private boolean canMonitorCalls() {
        return apiRegistry != null;
    }

    private Document normalCall(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        return monitoredConnection.sendDocument(aanvraag, xml);
    }

    private Document monitoredCall(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        final String url = magdaEndpoints.magdaUrl(aanvraag.magdaService());
        Optional<ApiDecoration> apiDecoration = apiRegistry.lookup(url);
        if (apiDecoration.isEmpty()) {
            return normalCall(aanvraag, xml);
        }

        URL parsedUrl = parseUrl(url);

        ApiLog apiLog = kpiLogs.api()
                .outgoing()
                .url(url)
                .path(parsedUrl.getPath())
                .query(parsedUrl.getQuery())
                .method(HttpMethod.POST.name())
                .attribute("referte", aanvraag.getRequestId().toString())
                .build();
        apiDecoration.get().decorate(apiLog);
        ManualRecorder<ApiLog> recorder = KpiLogs.manualRecorder(apiLog);
        recorder.start();
        try {
            Document document = monitoredConnection.sendDocument(aanvraag, xml);
            apiLog.setResponseCode(200);
            recorder.success();
            return document;
        } catch (MagdaSendFailed magdaSendFailed) {
            apiLog.setResponseCode(magdaSendFailed.getStatusCode());
            recorder.failure();
            throw magdaSendFailed;
        } catch (Throwable t) {
            recorder.failure();
            throw t;
        }
    }

    private URL parseUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
