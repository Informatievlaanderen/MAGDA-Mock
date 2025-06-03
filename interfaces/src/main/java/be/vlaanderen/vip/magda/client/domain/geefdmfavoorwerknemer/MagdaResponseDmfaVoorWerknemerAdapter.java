package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseDmfaVoorWerknemerAdapter {
    DmfaAttest adapt(MagdaResponseWrapper response) throws MagdaClientException;
}
