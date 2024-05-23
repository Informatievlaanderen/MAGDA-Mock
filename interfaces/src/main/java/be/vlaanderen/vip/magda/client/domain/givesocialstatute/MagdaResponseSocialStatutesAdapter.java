package be.vlaanderen.vip.magda.client.domain.givesocialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseSocialStatutesAdapter {
    SocialStatutes adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}
