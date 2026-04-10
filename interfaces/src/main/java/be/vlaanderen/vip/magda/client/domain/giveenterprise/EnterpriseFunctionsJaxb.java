package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions.EnterpriseFunctions;
import be.vlaanderen.vip.magda.client.domain.model.shared.CodeAndDescriptionJaxb;
import be.vlaanderen.vip.magda.client.domain.model.shared.LocalDateXmlAdapter;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Functies")
@Accessors(fluent = true)
public class EnterpriseFunctionsJaxb implements EnterpriseFunctions, Serializable {

    @XmlElement(name = "Functie")
    ArrayList<EnterpriseFunctionJaxbV2> functions = new ArrayList<>();

    @Override
    public List<EnterpriseFunction> enterpriseFunctions() {
        return functions.stream()
                .map(enterpriseFunctionJaxb -> new EnterpriseFunctionAdapter(enterpriseFunctionJaxb))
                .map(x -> (EnterpriseFunction) x)
                .toList();
    }

    @Override
    public List<EnterpriseFunctionV2> enterpriseFunctionsV2() {
        return List.copyOf(functions);
    }

    private static class EnterpriseFunctionAdapter implements EnterpriseFunction, Serializable {
        private final EnterpriseFunctionJaxbV2 source;

         EnterpriseFunctionAdapter(EnterpriseFunctionJaxbV2 source) {
            this.source = source;
        }

        @Override
        public String enterpriseNumber() {
            return source.functionHolderOf;
        }

        @Override
        public String personOrEnterpriseCode() {
            return source.entityType() != null ? source.entityType().codeValue() : null;
        }
    }

    private static class EnterpriseFunctionJaxbV2 implements EnterpriseFunctionV2, Serializable {

        @XmlAttribute(name = "DatumModificatie")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @Nullable
        LocalDate modificationDate;

        @XmlElement(name = "FunctiehouderVan")
        @Nullable
        String functionHolderOf;

        @XmlElement(name = "AuthentiekeBron")
        @Nullable
        CodeAndDescriptionJaxb source;

        @XmlElement(name = "AanduidingPersoonOnderneming")
        @Nullable
        CodeAndDescriptionJaxb entityType;

        @XmlElement(name = "INSZ")
        @Nullable
        String insz;

        @XmlElement(name = "Ondernemingsnummer")
        @Nullable
        String enterpriseNumber;

        @XmlElement(name = "VolgNummer")
        @Nullable
        String sequenceNumber;

        @XmlElement(name = "AardFunctie")
        @Nullable
        CodeAndDescriptionJaxb functionType;

        @XmlElement(name = "AardFunctieCAC")
        @Nullable
        CodeAndDescriptionJaxb functionTypeCAC;

        @XmlElement(name = "EindeFunctie")
        @Nullable
        CodeAndDescriptionJaxb endOfFunction;

        @XmlElement(name = "AanduidingVrijstelling")
        @Nullable
        CodeAndDescriptionJaxb exemptionStatus;

        @XmlElement(name = "Periode")
        @Nullable
        PeriodJaxb validityPeriod;

        @XmlElement(name = "PeriodeCAC")
        @Nullable
        PeriodJaxb validityPeriodCAC;

        @XmlElement(name = "SoortVrijstelling")
        @Nullable
        CodeAndDescriptionJaxb exemptionType;

        @Override
        public LocalDate modificationDate() {
            return modificationDate;
        }

        @Override
        public String enterpriseNumber() {
            return enterpriseNumber;
        }

        @Override
        public String functionHolderOf() {
            return functionHolderOf;
        }

        @Override
        public String insz() {
            return insz;
        }

        @Override
        public String sequenceNumber() {
            return sequenceNumber;
        }

        @Override
        public Enterprise.CodeAndDescription endOfFunction() {
            return endOfFunction;
        }

        @Override
        public Enterprise.CodeAndDescription functionType() {
            return functionType;
        }

        @Override
        public Enterprise.CodeAndDescription functionTypeCAC() {
            return functionTypeCAC;
        }

        @Override
        public Enterprise.CodeAndDescription source() {
            return source;
        }

        @Override
        public Enterprise.CodeAndDescription entityType() {
            return entityType;
        }

        @Override
        public Enterprise.CodeAndDescription exemptionStatus() {
            return exemptionStatus;
        }

        @Override
        public Enterprise.CodeAndDescription exemptionType() {
            return exemptionType;
        }

        @Override
        public Period period() {
            return validityPeriod;
        }

        @Override
        public Period periodCAC() {
            return validityPeriodCAC;
        }
    }

    @Getter
    private static class PeriodJaxb implements EnterpriseFunctions.Period, Serializable {

        @XmlElement(name = "Begin")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate getStartDate;

        @XmlElement(name = "Einde")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate getEndDate;
    }
}
