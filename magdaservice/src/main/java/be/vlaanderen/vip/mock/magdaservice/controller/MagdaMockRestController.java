package be.vlaanderen.vip.mock.magdaservice.controller;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.mock.magda.client.MagdaMockRestConnection;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class MagdaMockRestController {
    // Gemeenschappelijk endpoint voor alle MAGDA REST webservices
    private static final String MAGDA_REST_V1 = "Magda-v1/rest";

    private final MagdaConnection magdaMockRestConnection;
    public MagdaMockRestController(@Qualifier("magdaRestConnection") MagdaConnection magdaMockRestConnection) {
        this.magdaMockRestConnection = magdaMockRestConnection;
    }

    @RequestMapping(
            value = {MAGDA_REST_V1 + "/**", "api/" + MAGDA_REST_V1 + "/**"},
            produces = {APPLICATION_JSON_VALUE}, consumes = {APPLICATION_JSON_VALUE},
            method = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT}
    )
    protected ResponseEntity<String> magdaRestEndpoint(@RequestBody(required = false) String requestBody, HttpServletRequest incomingRequest) throws MagdaConnectionException {
        requestBody = requestBody == null ? "" : requestBody;
        String method = incomingRequest.getMethod();
        List<String> splittedRequestUri = new ArrayList<>(Arrays.stream(incomingRequest.getRequestURI().split(Pattern.quote(MAGDA_REST_V1))).toList());
        String query = incomingRequest.getQueryString();
        splittedRequestUri.remove(0);
        String path = String.join(MAGDA_REST_V1, splittedRequestUri);
        Pair<JsonNode, Integer> response = magdaMockRestConnection.sendRestRequest(path, query, method, requestBody);
        return new ResponseEntity<>(response.getLeft().toString(), HttpStatusCode.valueOf(response.getRight()));
    }

}
