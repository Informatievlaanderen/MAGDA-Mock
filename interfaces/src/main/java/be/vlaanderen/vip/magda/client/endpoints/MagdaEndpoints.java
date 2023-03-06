package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;

import java.net.URI;

public interface MagdaEndpoints { // XXX maybe remove entirely?
    URI magdaUri(MagdaServiceIdentificatie aanvraag);
}
