package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import be.vlaanderen.vip.magda.client.domain.shared.AddressJaxb;
import be.vlaanderen.vip.magda.client.domain.shared.OffsetDateTimeXmlAdapter;
import be.vlaanderen.vip.magda.client.domain.shared.OffsetDateXmlAdapter;
import be.vlaanderen.vip.magda.client.domain.shared.ValueAndDescriptionJaxb;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@XmlRootElement(name = "Inhoud")
@Accessors(fluent = true)
public class WorkRelationsJaxb implements WorkRelations, Serializable {

    @XmlElementWrapper(name = "Contracten")
    @XmlElement(name = "Contract")
    ArrayList<Contract> contracts;

    @Override
    public List<WorkRelations.Contract> contracts() {
        return contracts.stream().map(o -> (WorkRelations.Contract) o).toList();
    }

    @Getter
    private static class Contract implements WorkRelations.Contract, Serializable {
        @XmlElement(name = "Relatie")
        Relation relation;

        @XmlElement(name = "Bron")
        ValueAndDescriptionJaxb source;

        @XmlElement(name = "Aangifte")
        Declaration declaration;

        @XmlElement(name = "InterimOnderneming")
        InterimEnterprise interimEnterprise;

        @XmlElement(name = "PlaatsTewerkstelling")
        PlaceOfEmployment placeOfEmployment;

        @Getter
        private static class Relation implements WorkRelations.Relation, Serializable {
            @XmlElement(name = "Werkgever")
            Employer employer;

            @XmlElement(name = "Werknemer")
            Employee employee;

            @Getter
            private static class Employer implements WorkRelations.Employer, Serializable {
                @XmlElement(name = "RSZNummer")
                String rszNumber;

                @XmlElement(name = "Ondernemingsnummer")
                String enterpriseNumber;

                @XmlElement(name = "DeelEntiteit")
                String subEntity;
            }

            @Getter
            private static class Employee implements WorkRelations.Employee, Serializable {
                @XmlElement(name = "INSZ")
                String insz;

                @XmlElement(name = "IdentificatiePersoonGevalideerd")
                ValueAndDescriptionJaxb identificationPersonValidated;

                @XmlElement(name = "Achternaam")
                String lastName;

                @XmlElement(name = "Voornaam")
                String firstName;

            }

        }


        @Getter
        private static class Declaration implements WorkRelations.Declaration, Serializable {
            @XmlElement(name = "DimonaNummer")
            String dimonaNumber;

            @XmlElement(name = "PeriodeTijdstip")
            Period period = new Period();

            @XmlElement(name = "ParitairComite")
            String jointCommittee;

            @XmlElement(name = "AardWerknemer")
            String natureOfEmployee;

            @XmlElement(name = "ServiceCategorie")
            ValueAndDescriptionJaxb serviceCategory;

            @XmlElement(name = "AantalGeplandeWerkdagen")
            Integer numberOfPlannedWorkDays;

            @XmlElement(name = "Geannuleerd")
            Boolean cancelled;

            @XmlElement(name = "LaatsteDimonaActie")
            ValueAndDescriptionJaxb lastDimonaAction;

            @XmlElement(name = "Controlekaarten")
            ControlCards controlCards;

            @XmlAttribute(name = "DatumTijdstipCreatie")
            @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
            OffsetDateTime dateTimeOfCreation;
            @XmlAttribute(name = "DatumTijdstipModificatie")
            @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
            OffsetDateTime dateTimeOfModification;

            private static class Period implements WorkRelations.Period, Serializable {
                @XmlElement(name = "Begin")
                Date startDate;

                @XmlElement(name = "Einde")
                Date endDate;

                @Override
                public OffsetDateTime startDate() {
                    return Optional.ofNullable(startDate).map(o -> o.date).orElse(null);
                }

                @Override
                public OffsetDateTime endDate() {
                    return Optional.ofNullable(endDate).map(o -> o.date).orElse(null);
                }

                private static class Date implements Serializable {
                    @XmlElement(name = "Datum")
                    @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
                    OffsetDateTime date;
                }

            }

            @Getter
            private static class ControlCards implements WorkRelations.ControlCards, Serializable {
                @XmlElement(name = "KaartnummerEersteMaand")
                String cardNumberFirstMonth;

                @XmlElement(name = "KaartnummerVolgendeMaand")
                String cardNumberNextMonth;
            }
        }

        @Getter
        private static class InterimEnterprise implements WorkRelations.InterimEnterprise, Serializable {
            @XmlElement(name = "Naam")
            String name;
            @XmlElement(name = "RSZNummer")
            String rszNumber;
            @XmlElement(name = "Ondernemingsnummer")
            String enterpriseNumber;
            @XmlElement(name = "ParitairComite")
            String jointCommittee;
        }

        @Getter
        private static class PlaceOfEmployment implements WorkRelations.PlaceOfEmployment, Serializable {
            @XmlElement(name = "Naam")
            String name;

            @XmlElement(name = "Adres")
            Address address;

            private static class Address implements WorkRelations.Address, Serializable {
                @XmlElement(name = "Straat")
                AddressJaxb.Street street;

                @XmlElement(name = "Huisnummer")
                @Getter
                String houseNumber;

                @XmlElement(name = "Busnummer")
                @Getter
                String busNumber;

                @XmlElement(name = "Gemeente")
                @Getter
                AddressJaxb.Municipality municipality;

                @XmlElement(name = "Land")
                @Getter
                AddressJaxb.Country country;


                @Override
                public String street() {
                    return Optional.ofNullable(street)
                            .map(AddressJaxb.Street::name)
                            .orElse(null);
                }

            }

        }

    }

}