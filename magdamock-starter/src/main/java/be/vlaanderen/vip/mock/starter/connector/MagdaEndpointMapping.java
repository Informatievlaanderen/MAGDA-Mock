package be.vlaanderen.vip.mock.starter.connector;

import java.net.URI;
import java.util.List;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoint;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import lombok.Data;

@Data
public class MagdaEndpointMapping {
    private String name;
    private String version;
    private URI path;
    
    public static MagdaEndpoints toMagdaEndpoints(List<MagdaEndpointMapping> endpoints) {
        var remoteEndpointsBuilder = MagdaEndpoints.builder();
        endpoints.forEach(e -> remoteEndpointsBuilder.addMapping(e.getName(),
                                                                 e.getVersion(),
                                                                 MagdaEndpoint.of(e.getPath())));
        return remoteEndpointsBuilder.build();
    }

}
