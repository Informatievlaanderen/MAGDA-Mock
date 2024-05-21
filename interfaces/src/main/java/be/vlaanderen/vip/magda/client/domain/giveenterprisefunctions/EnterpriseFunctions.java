package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import java.util.List;

public interface EnterpriseFunctions {

    List<EnterpriseFunction> enterpriseFunctions();

    interface EnterpriseFunction {

        String enterpriseNumber();

        String personOrEnterpriseCode();
    }
}