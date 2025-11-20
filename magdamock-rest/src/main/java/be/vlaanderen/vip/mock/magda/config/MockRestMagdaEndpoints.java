package be.vlaanderen.vip.mock.magda.config;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;

public record MockRestMagdaEndpoints(URI uri) implements MagdaEndpoints {
    @SneakyThrows
    @Override
    public URI magdaUri(MagdaServiceIdentification serviceId) {
        if (serviceId.getName().equals("mobility-registrations")) {
            return new URIBuilder(uri).appendPath("/v1/mobility/registrations").build();
        }
        return uri;
    }
}