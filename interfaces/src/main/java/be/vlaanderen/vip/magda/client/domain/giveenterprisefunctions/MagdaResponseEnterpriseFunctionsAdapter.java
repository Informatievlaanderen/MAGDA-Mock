package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseEnterpriseFunctionsAdapter {
    EnterpriseFunctions adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}
