package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;

import java.time.LocalDate;
import java.util.List;

public interface EnterpriseFunctions {

    @Deprecated
    List<EnterpriseFunction> enterpriseFunctions();

    @Deprecated
    interface EnterpriseFunction {

        String enterpriseNumber();

        String personOrEnterpriseCode();
    }

    List<EnterpriseFunctionV2> enterpriseFunctionsV2();

    interface EnterpriseFunctionV2 {

        String enterpriseNumber();

        String functionHolderOf();

        String insz();

        String sequenceNumber();

        Enterprise.CodeAndDescription endOfFunction();

        Enterprise.CodeAndDescription functionType();

        Enterprise.CodeAndDescription functionTypeCAC();

        Enterprise.CodeAndDescription source();

        Enterprise.CodeAndDescription entityType();

        Enterprise.CodeAndDescription exemptionStatus();

        Enterprise.CodeAndDescription exemptionType();

        Period period();

        Period periodCAC();
    }

    interface Period {

        LocalDate getStartDate();

        LocalDate getEndDate();
    }
}