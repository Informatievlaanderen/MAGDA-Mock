package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.exception.ServerException;
import org.apache.hc.core5.http.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestMobilityRegistrationService implements MobilityRegistrationService {
    private static final MagdaServiceIdentification dienst = new MagdaServiceIdentification("REST /v1/mobility/registrations", "00.01");
    private final MobilityRegistrationAdapter adapter;
    private final MagdaClient client;

    public RestMobilityRegistrationService(MagdaClient client, MobilityRegistrationAdapter adapter) {
        this.client = client;
        this.adapter = adapter;
    }

    @Override
    public List<Registration> getRegistrations(MobilityRegistrationRequest request) throws MagdaClientException {
        Map<String, String> headers = new HashMap<>();
        if (request.getHttpDateHeader() != null && !request.getHttpDateHeader().isEmpty()){
            headers.put("Date", request.getHttpDateHeader());
        }
        MagdaRestRequest restRequest = MagdaRestRequest.builder()
                .dienst(dienst)
                .method(Method.GET)
                .senderId(request.getRegistrationInfo().getIdentification())
                .senderQualityCode(request.getRegistrationInfo().getHoedanigheidscode().orElse(""))
                .correlationId(request.getCorrelationId().toString())
                .enduserId(request.getEnduserId())
                .urlQueryParams(request.getQueryParameters())
                .headers(headers)
                .build();
        try {
            return adapter.adapt(client.sendRestRequest(restRequest));
        } catch (ServerException e) {
            throw new MagdaClientException("Registrations call went wrong: ", e);
        }
    }
}
