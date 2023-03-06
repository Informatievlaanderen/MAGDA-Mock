package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;

import java.net.URI;

public interface MagdaEndpoints {
    URI magdaUri(MagdaServiceIdentificatie aanvraag);

    static Builder builder() {
        return new Builder();
    }

    class Builder { // XXX use everywhere

        private ServiceMappedMagdaEndpoints serviceMappedMagdaEndpoints;

        public Builder() {
            this.serviceMappedMagdaEndpoints = new ServiceMappedMagdaEndpoints();
        }

        public Builder withMapping(String dienstNaam, String versie, MagdaEndpoint magdaEndpoint) {
            serviceMappedMagdaEndpoints.addMapping(dienstNaam, versie, magdaEndpoint);
            return this;
        }

        public MagdaEndpoints build() {
            return serviceMappedMagdaEndpoints;
        }
    }
}