package be.vlaanderen.vip.magda.client.domain.givearzacareer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseARZACareerAdapter {
    ARZACareer adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}
