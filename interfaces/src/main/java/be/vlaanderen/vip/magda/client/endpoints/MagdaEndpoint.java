package be.vlaanderen.vip.magda.client.endpoints;

import lombok.Getter;

import java.net.URL;

@Getter
public class MagdaEndpoint {
    private final String path;
    private final URL tni;
    private final URL prod;

    public MagdaEndpoint(URL tni, URL prod) {
        this.tni = tni;
        this.prod = prod;
        path = tni.getPath();
    }

}
