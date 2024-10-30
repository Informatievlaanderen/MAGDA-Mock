package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

public interface MagdaResponseAnnualAccountsAdapter {
    AnnualAccounts adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;

}
