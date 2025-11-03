package be.vlaanderen.vip.magda.client.rest;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import lombok.Builder;
import lombok.Getter;
import org.apache.hc.core5.http.Method;

import java.util.Map;

@Builder
@Getter
public class MagdaRestRequest {
    private MagdaServiceIdentification dienst;
    private Method method;
    private Map<String, String> urlQueryParams;
    private Map<String, String> headers;
}
