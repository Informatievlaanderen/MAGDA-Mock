package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions.EnterpriseFunctions;
import be.vlaanderen.vip.magda.client.domain.shared.CodeAndDescriptionJaxb;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Functies")
@Accessors(fluent = true)
public class EnterpriseFunctionsJaxb implements EnterpriseFunctions, Serializable {
    @XmlElement(name = "Functie")
    ArrayList<EnterpriseFunctionJaxb> functions = new ArrayList<>();

    @Override
    public List<EnterpriseFunction> enterpriseFunctions() {
        return functions.stream().map(x -> (EnterpriseFunction) x).toList();
    }

    private static class EnterpriseFunctionJaxb implements EnterpriseFunctions.EnterpriseFunction, Serializable {
        @XmlElement(name = "FunctiehouderVan")
        @Getter
        String enterpriseNumber;

        @XmlElement(name = "AanduidingPersoonOnderneming")
        CodeAndDescriptionJaxb personOrEnterpriseCode;

        @Override
        public String personOrEnterpriseCode() {
            return personOrEnterpriseCode.codeValue();
        }
    }
}
