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
        if (serviceId.equals(new MagdaServiceIdentification("REST /v1/mobility/registrations", "00.01"))) {
            return new URIBuilder(uri).appendPath("/v1/mobility/registrations").build();
        } else if (serviceId.equals(new MagdaServiceIdentification("REST /v1/socZek/handicap/volledigeDossiers", "00.01"))) {
            return new URIBuilder(uri).appendPath("/v1/socZek/handicap/volledigeDossiers").build();
        }
        return uri;
    }
}