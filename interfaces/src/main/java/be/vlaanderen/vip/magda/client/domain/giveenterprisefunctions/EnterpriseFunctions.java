package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;

import java.time.LocalDate;
import java.util.List;

public interface EnterpriseFunctions {

    List<EnterpriseFunction> enterpriseFunctions();

    interface EnterpriseFunction {

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