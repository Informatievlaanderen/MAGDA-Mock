package be.vlaanderen.vip.magda.client.domain.shared;

import be.vlaanderen.vip.magda.client.domain.givearzacareer.ARZACareer;
import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlValue;

import java.io.Serializable;

public class CodeAndDescriptionJaxb implements Enterprise.CodeAndDescription, ARZACareer.CodeAndDescription, Serializable {
    @XmlElement(name = "Code")
    String value;
    @XmlAttribute(name = "Beschrijving")
    String codeDescription;
    @XmlElement(name = "Omschrijving")
    Description description;

    @Override
    public String codeValue() {
        return value;
    }

    @Override
    public String codeDescription() {
        return codeDescription;
    }

    @Override
    public String descriptionValue() {
        return description.value;
    }

    @Override
    public String descriptionOrigin() {
        return description.origin;
    }

    @Override
    public String descriptionLanguageCode() {
        return description.languageCode;
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
