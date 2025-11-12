package be.vlaanderen.vip.mock.magdaservice.controller;

import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.mock.magdaservice.services.MockRestClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class MagdaMockRestController {
    // Gemeenschappelijk endpoint voor alle MAGDA REST webservices
    private static final String MAGDA_REST_V1 = "Magda-v1/rest";

    private final MockRestClient mockRestClient;
    public MagdaMockRestController(MockRestClient mockRestClient) {
        this.mockRestClient = mockRestClient;
    }

    @RequestMapping(
            value = {MAGDA_REST_V1 + "/**", "api/" + MAGDA_REST_V1 + "/**"},
            produces = {APPLICATION_JSON_VALUE}, consumes = {APPLICATION_JSON_VALUE},
            method = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT}
    )
    protected ResponseEntity<String> magdaRestEndpoint(@RequestBody String requestBody, HttpServletRequest incomingRequest) throws MagdaConnectionException {
        String method = incomingRequest.getMethod();
        List<String> splittedRequestUri = new ArrayList<>(Arrays.stream(incomingRequest.getRequestURI().split(MAGDA_REST_V1)).toList());
        String query = incomingRequest.getQueryString();
        splittedRequestUri.remove(0);
        String path = String.join("", splittedRequestUri);
        return mockRestClient.processRestRequest(path, query, method, requestBody);
    }

}
