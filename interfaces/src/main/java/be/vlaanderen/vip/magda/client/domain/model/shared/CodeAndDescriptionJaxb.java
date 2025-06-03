package be.vlaanderen.vip.magda.client.domain.model.shared;

import be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer.DmfaAttest;
import be.vlaanderen.vip.magda.client.domain.giveannualaccounts.AnnualAccounts;
import be.vlaanderen.vip.magda.client.domain.givearzacareer.ARZACareer;
import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlValue;

import java.io.Serializable;

public class CodeAndDescriptionJaxb implements DmfaAttest.CodeAndDescription, Enterprise.CodeAndDescription, ARZACareer.CodeAndDescription, AnnualAccounts.CodeAndDescription, Serializable {
    @XmlElement(name = "Code")
    Code code;
    @XmlElement(name = "Omschrijving")
    Description description;

    @Override
    public String codeValue() {
        return code != null ? code.value : null;
    }

    @Override
    public String codeDescription() {
        return code != null ? code.description : null;
    }

    @Override
    public String descriptionValue() {
        return description != null ? description.value : null;
    }

    @Override
    public String descriptionOrigin() {
        return description != null ? description.origin : null;
    }

    @Override
    public String descriptionLanguageCode() {
        return description != null ? description.languageCode : null;
    }

    private static class Code implements Serializable {
        @XmlValue
        String value;
        @XmlAttribute(name = "Beschrijving")
        String description;
    }

    private static class Description implements Serializable {
        @XmlValue
        String value;
        @XmlAttribute(name = "Oorsprong")
        String origin;
        @XmlAttribute(name = "TaalCode")
        String languageCode;
    }

}
