package be.vlaanderen.vip.magda.client.endpoints;

import lombok.Value;

import java.net.URI;

@Value
public class MagdaEndpoint {

    private URI uri;

    private MagdaEndpoint(URI uri) {
        this.uri = uri;
    }

    public static MagdaEndpoint of(URI uri) {
        if(!uri.isAbsolute()) {
            throw new IllegalArgumentException("A Magda endpoint must be an absolute URI.");
        }

        return new MagdaEndpoint(uri);
    }

    public static MagdaEndpoint of(String uriString) {
        return of(URI.create(uriString));
    }
}
