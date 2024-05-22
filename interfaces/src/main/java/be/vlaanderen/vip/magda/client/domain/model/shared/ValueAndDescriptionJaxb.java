package be.vlaanderen.vip.magda.client.domain.model.shared;

import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import be.vlaanderen.vip.magda.client.domain.giveworkrelations.WorkRelations;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
public class ValueAndDescriptionJaxb implements WorkRelations.ValueAndDescription, Enterprise.ValueAndDescription, Serializable {
    @XmlValue
    String value;
    @XmlAttribute(name = "Beschrijving")
    String description;
}
