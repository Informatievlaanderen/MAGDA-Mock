package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.annotation.Nullable;

public interface MagdaResponseEnterpriseAdapter {

    @Nullable
    Enterprise adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}