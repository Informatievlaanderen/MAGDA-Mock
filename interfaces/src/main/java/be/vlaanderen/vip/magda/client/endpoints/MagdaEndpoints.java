package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;

import java.net.URI;

public interface MagdaEndpoints {
    URI magdaUri(MagdaServiceIdentification serviceId);

    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private final ServiceMappedMagdaEndpoints serviceMappedMagdaEndpoints;

        public Builder() {
            this.serviceMappedMagdaEndpoints = new ServiceMappedMagdaEndpoints();
        }

        public Builder addMapping(String name, String version, MagdaEndpoint magdaEndpoint) {
            serviceMappedMagdaEndpoints.addMapping(name, version, magdaEndpoint);
            return this;
        }

        public MagdaEndpoints build() {
            return serviceMappedMagdaEndpoints;
        }
    }
}