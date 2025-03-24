package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.domain.model.shared.CodeAndDescriptionJaxb;
import be.vlaanderen.vip.magda.client.domain.model.shared.YearXmlAdapter;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Jaarrekeningen")
@Accessors(fluent = true)
@Getter
public class AnnualAccountsJaxb implements AnnualAccounts, Serializable {

    @XmlElement(name = "Jaarrekening")
    ArrayList<AnnualAccountJaxb> annualAccount = new ArrayList<>();

    @Override
    public List<AnnualAccounts.AnnualAccount> annualAccounts() {
        return annualAccount.stream()
                .map(x -> (AnnualAccounts.AnnualAccount) x)
                .toList();
    }

    @Getter
    private static class AnnualAccountJaxb implements AnnualAccounts.AnnualAccount, Serializable {

        @XmlElement(name = "Hoofding")
        @Nullable
        HeaderJaxb header;

        @XmlElementWrapper(name = "Elementen")
        @XmlElement(name = "Element")
        ArrayList<ElementJaxb> elements = new ArrayList<>();

        @Override
        public List<AnnualAccounts.Element> elements() {
            return elements.stream()
                    .map(x -> (AnnualAccounts.Element) x)
                    .toList();
        }
    }

    @Getter
    private static class HeaderJaxb implements AnnualAccounts.Header, Serializable {

        @XmlElement(name = "Boekjaar")
        FinancialYearJaxb financialYear;

        @XmlElement(name = "Schema")
        CodeAndDescriptionJaxb schema;

        @XmlElement(name = "Soortschema")
        CodeAndDescriptionJaxb typeSchema;

        @XmlElement(name = "Aard")
        CodeAndDescriptionJaxb nature;
    }

    @Getter
    private static class ElementJaxb implements AnnualAccounts.Element, Serializable {

        @XmlElement(name = "Rubriek")
        String rubric;

        @XmlElement(name = "AantalBedrag")
        NumberAmountJaxb numberAmount;
    }

    @Getter
    private static class NumberAmountJaxb implements AnnualAccounts.NumberAmount, Serializable {

        @XmlElement(name = "Waarde")
        String value;
    }

    @Getter
    private static class FinancialYearJaxb implements AnnualAccounts.FinancialYear, Serializable {

        @XmlElement(name = "Jaar")
        @XmlJavaTypeAdapter(YearXmlAdapter.class)
        Year year;
    }
}