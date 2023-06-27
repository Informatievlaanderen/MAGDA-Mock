package be.vlaanderen.vip.magda.client.endpoints;

import lombok.Getter;

import java.net.URI;

/**
 * The subset of URIs that are valid as MAGDA endpoint URIs.
 * The only additional criterion for validity is that the URI be absolute.
 */
@Getter
public class MagdaEndpoint {

    private final URI uri;

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
