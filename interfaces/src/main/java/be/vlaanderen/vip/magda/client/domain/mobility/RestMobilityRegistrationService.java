package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.rest.MagdaRestClient;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import org.apache.hc.core5.http.Method;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class RestMobilityRegistrationService implements MobilityRegistrationService {
    private static final MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
    private final MagdaRestClient magdaRestClient;
    private final MobilityRegistrationAdapter adapter;

    public RestMobilityRegistrationService(MagdaRestClient magdaRestClient, MobilityRegistrationAdapter adapter) {
        this.magdaRestClient = magdaRestClient;
        this.adapter = adapter;
    }

    @Override
    public List<Registration> getRegistrations(MobilityRegistrationRequest request) throws MagdaClientException {
        MagdaRestRequest restRequest = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .senderId(request.getRegistrationInfo().getIdentification())
                .senderQualityCode(request.getRegistrationInfo().getHoedanigheidscode().orElse(""))
                .correlationId(request.getCorrelationId().toString())
                .enduserId(request.getEnduserId())
                .urlQueryParams(request.getQueryParameters())
                .build();
        try {
            return adapter.adapt(magdaRestClient.sendRestRequest(restRequest));
        } catch (URISyntaxException | MagdaConnectionException e) {
            throw new MagdaClientException("Registrations call went wrong: ",e);
        }
    }
}
