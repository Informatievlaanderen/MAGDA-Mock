package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseDisabilityDossierAdapter {
    DisabilityDossier adapt(MagdaResponseWrapper response) throws MagdaClientException;
}
