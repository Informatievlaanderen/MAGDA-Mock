package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import java.time.Year;
import java.util.List;

public interface AnnualAccounts {

    List<AnnualAccount> annualAccounts();

    interface AnnualAccount {

        Header header();

        List<Element> elements();
    }

    interface Header {

        FinancialYear financialYear();

        CodeAndDescription schema();

        CodeAndDescription typeSchema();

        CodeAndDescription nature(); // XXX is this a code and description? not sure
    }

    interface Element {

        String rubric();

        NumberAmount numberAmount();
    }

    interface NumberAmount {

        String value();
    }

    interface FinancialYear {

        Year year();
    }

    interface CodeAndDescription {

        String codeValue();

        String codeDescription();

        String descriptionValue();

        String descriptionOrigin();

        String descriptionLanguageCode();
    }
}