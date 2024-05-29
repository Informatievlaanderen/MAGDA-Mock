package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseWorkRelationsAdapter {
    WorkRelations adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}
