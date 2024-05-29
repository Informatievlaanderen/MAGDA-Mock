package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseEnterpriseAdapter {
    Enterprise adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;

}
