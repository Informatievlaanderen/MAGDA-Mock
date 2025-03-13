package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.annotation.Nullable;

import java.time.Year;
import java.util.List;

public interface AnnualAccounts {

    static AnnualAccounts ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return new MagdaResponseAnnualAccountsAdapterJaxbImpl().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    List<AnnualAccount> annualAccounts();

    interface AnnualAccount {

        @Nullable
        Header header();

        List<Element> elements();
    }

    interface Header {

        FinancialYear financialYear();

        CodeAndDescription schema();

        CodeAndDescription typeSchema();

        CodeAndDescription nature();
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