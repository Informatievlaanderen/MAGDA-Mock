package be.vlaanderen.vip.magda.client.domain.model.shared;

import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import be.vlaanderen.vip.magda.client.domain.giveworkrelations.WorkRelations;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Accessors(fluent = true)
@Getter
public class AddressJaxb implements Enterprise.BaseAddress, Serializable {
    @XmlElement(name = "Straat")
    Street street;
    @XmlElement(name = "Huisnummer")
    String houseNumber;
    @XmlElement(name = "Busnummer")
    String busNumber;
    @XmlElement(name = "Gemeente")
    Municipality municipality;
    @XmlElement(name = "Staat")
    String state;
    @XmlElement(name = "Land")
    Country country;

    @XmlAttribute(name = "DatumBegin")
    @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
    OffsetDateTime startDate;

    @XmlAttribute(name = "DatumEinde")
    @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
    OffsetDateTime endDate;

    @Getter
    public static class Country implements Enterprise.Country, WorkRelations.Country, Serializable {
        @XmlElement(name = "NISCode")
        String nisCode;
        @XmlElement(name = "Naam")
        String name;
        @XmlElement(name = "ISOCode")
        String isoCode;
    }

    @Getter
    public static class Street implements Enterprise.Street, Serializable {
        @XmlElement(name = "Code")
        String code;
        @XmlElement(name = "Naam")
        String name;
    }

    @Getter
    public static class Municipality implements Enterprise.Municipality, WorkRelations.Municipality, Serializable {
        @XmlElement(name = "NISCode")
        String nisCode;
        @XmlElement(name = "PostCode")
        Integer postCode;
        @XmlElement(name = "Naam")
        String name;
    }


}
