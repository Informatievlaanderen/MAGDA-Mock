package be.vlaanderen.vip.magda.client.domain.model.enterprise;

import be.vlaanderen.vip.magda.client.domain.giveenterprise.Enterprise;
import be.vlaanderen.vip.magda.client.domain.model.shared.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Onderneming")
@Accessors(fluent = true)
public class EnterpriseJaxb implements Enterprise, Serializable {
    @XmlElement(name = "Ondernemingsnummer")
    @Getter
    String enterpriseNumber;

    @XmlElementWrapper(name = "Vestigingen")
    @XmlElement(name = "Vestiging")
    ArrayList<BranchOffice> branchOffices = new ArrayList<>();

    @XmlElementWrapper(name = "Adressen")
    @XmlElement(name = "Adres")
    ArrayList<BaseAddress> addresses = new ArrayList<>();

    @XmlElement(name = "StatusKBO")
    @Getter
    CodeAndDescriptionJaxb statusKBO;

    @XmlElement(name = "Start")
    @Getter
    DateContainerJaxb startDate;

    @XmlElement(name = "Namen")
    @Getter
    CompanyNames companyNames;

    @XmlElement(name = "SoortOnderneming")
    @Getter
    CodeAndDescriptionJaxb enterpriseType;

    @XmlElementWrapper(name = "Rechtsvormen")
    @XmlElement(name = "Rechtsvorm")
    ArrayList<CodeAndDescriptionJaxb> legalForms = new ArrayList<>();

    @Override
    public List<Enterprise.BranchOffice> branchOffices() {
        return branchOffices.stream().map(o -> (Enterprise.BranchOffice) o).toList();
    }

    @Override
    public List<Enterprise.CodeAndDescription> legalForms() {
        return legalForms.stream().map(o -> (Enterprise.CodeAndDescription) o).toList();
    }

    @Override
    public List<Address> addresses() {
        return addresses.stream().map(o -> (Enterprise.Address) o).toList();
    }

    @Getter
    private static class CompanyNames implements Enterprise.CompanyNames, Serializable {

        @XmlElementWrapper(name = "MaatschappelijkeNamen")
        @XmlElement(name = "MaatschappelijkeNaam")
        List<CompanyName> registeredNames;

        @Override
        public List<Enterprise.CompanyName> registeredNames() {
            return registeredNames.stream().map(o -> (Enterprise.CompanyName) o).toList();
        }
    }

    @Getter
    private static class CompanyName implements Enterprise.CompanyName, Serializable {

        @XmlElement(name = "Naam")
        String name;

        @XmlElement(name = "Taalcode")
        String languageCode;

        @Override
        public OffsetDateTime startDate() {
            return null;
        }

        @Override
        public OffsetDateTime endDate() {
            return null;
        }
    }

    private static class BranchOffice implements Enterprise.BranchOffice, Serializable {
        @XmlElementWrapper(name = "Adressen")
        @XmlElement(name = "Adres")
        ArrayList<OfficeAddress> officeAddresses;
        @XmlAttribute(name = "DatumBegin")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        @Getter
        OffsetDateTime startDate;
        @XmlAttribute(name = "DatumEinde")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        @Getter
        OffsetDateTime endDate;

        @Override
        public List<Address> addresses() {
            return officeAddresses.stream().map(o -> (Enterprise.Address) o).toList();
        }

        @Getter
        private static class OfficeAddress implements Enterprise.Address, Serializable {
            @XmlElement(name = "Gemeente")
            AddressJaxb.Municipality municipality;
            @XmlAttribute(name = "DatumBegin")
            @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
            OffsetDateTime startDate;
            @XmlAttribute(name = "DatumEinde")
            @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
            OffsetDateTime endDate;

            @Override
            public Street street() {
                return null;
            }

            @Override
            public String houseNumber() {
                return null;
            }

            @Override
            public String busNumber() {
                return null;
            }

            @Override
            public String state() {
                return null;
            }

            @Override
            public Country country() {
                return null;
            }

            @Override
            public Type type() {
                return null;
            }

            @Override
            public List<Description> descriptions() {
                return Collections.emptyList();
            }

            @Override
            public List<Cancellation> cancellations() {
                return Collections.emptyList();
            }
        }

    }

    private static class BaseAddress extends AddressJaxb implements Enterprise.Address, Serializable {
        @XmlElement(name = "Type")
        @Getter
        Type type;
        @XmlElementWrapper(name = "Descripties")
        @XmlElement(name = "Descriptie")
        ArrayList<Descriptie> descripties = new ArrayList<>();
        @XmlElementWrapper(name = "Doorhalingen")
        @XmlElement(name = "Doorhaling")
        ArrayList<Cancellation> cancellations = new ArrayList<>();

        @Override
        public List<Description> descriptions() {
            return descripties.stream().map(o -> (Enterprise.Description) o).toList();
        }

        @Override
        public List<Enterprise.Cancellation> cancellations() {
            return cancellations.stream().map(o -> (Enterprise.Cancellation) o).toList();
        }

        @Getter
        private static class Type implements Enterprise.Type, Serializable {
            @XmlElement(name = "Code")
            ValueAndDescriptionJaxb code;
            @XmlElement(name = "Omschrijving")
            TypeDescription description;

            @Getter
            private static class TypeDescription implements Enterprise.TypeDescription, Serializable {
                @XmlValue
                String value;
                @XmlAttribute(name = "Oorsprong")
                String origin;
                @XmlAttribute(name = "TaalCode")
                String languageCode;
            }

        }

        @Getter
        private static class Descriptie implements Enterprise.Description, Serializable {
            @XmlElement(name = "Adres")
            AddressJaxb address;
            @XmlElement(name = "Contact")
            ContactInfo contactInfo;
            @XmlElement(name = "Taalcode")
            String languageCode;
            @XmlElement(name = "Aanvulling")
            String supplement;

            @Getter
            private static class ContactInfo implements Enterprise.ContactInfo, Serializable {
                @XmlElement(name = "Telefoonnummer")
                String phoneNumber;
                @XmlElement(name = "Faxnummer")
                String faxNumber;
                @XmlElement(name = "GSM")
                String mobileNumber;
                @XmlElement(name = "Email")
                String emailAddress;
                @XmlElement(name = "Website")
                String website;
            }
        }

        @Getter
        private static class Cancellation implements Enterprise.Cancellation, Serializable {
            @XmlElement(name = "Reden")
            CodeAndDescriptionJaxb reason;
            @XmlAttribute(name = "DatumBegin")
            @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
            OffsetDateTime startDate;
            @XmlAttribute(name = "DatumEinde")
            @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
            OffsetDateTime endDate;
        }

    }

    @Getter
    private static class DateContainerJaxb implements Enterprise.DateContainer, Serializable {

        @XmlElement(name = "Datum")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate value;
    }
}