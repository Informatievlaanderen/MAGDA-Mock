package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource;
import be.vlaanderen.vip.magda.client.domain.model.shared.LocalDateXmlAdapter;
import be.vlaanderen.vip.magda.client.domain.model.shared.YearXmlAdapter;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Inhoud")
@Accessors(fluent = true)
public class EnrollmentHistoryJaxb implements EnrollmentHistory, Serializable {

    @Serial
    private static final long serialVersionUID = 7417543781799245182L;

    @XmlElementWrapper(name = "Inschrijvingen")
    @XmlElement(name = "Inschrijving")
    ArrayList<EnrollmentJaxb> enrollments;

    @Override
    public List<Enrollment> enrollments() {
        return enrollments.stream()
                .map(x -> (Enrollment) x)
                .toList();
    }

    @Getter
    private static class EnrollmentJaxb implements Enrollment, Serializable {

        @Serial
        private static final long serialVersionUID = 2123983013630220684L;

        @XmlElement(name = "Bron")
        EducationEnrollmentSource source;

        @XmlElement(name = "Referte")
        String reference;

        @XmlElement(name = "Instelling")
        InstitutionJaxb institution;

        @XmlElement(name = "OnderwijsLocatie")
        @Nullable
        EducationLocationJaxb educationLocation;

        @XmlElement(name = "HogerOnderwijs")
        @Nullable
        HigherEducationJaxb higherEducation;

        @XmlElement(name = "LeerplichtOnderwijs")
        @Nullable
        CompulsoryEducationJaxb compulsoryEducation;
    }

    @Getter
    private static class InstitutionJaxb implements Institution, Serializable {

        @Serial
        private static final long serialVersionUID = -1510011456863582497L;

        @XmlElement(name = "Instellingsnummer")
        String institutionNumber;

        @XmlElement(name = "OfficieleNaam")
        String officialName;

        @XmlElement(name = "Instantie")
        @Nullable
        String agency;

        @XmlElement(name = "Adres")
        @Nullable
        AddressJaxb address;

        @XmlElement(name = "Contactgegevens")
        @Nullable
        ContactInfoJaxb contactInfo;
    }

    @Getter
    private static class EducationLocationJaxb implements EducationLocation, Serializable {

        @Serial
        private static final long serialVersionUID = -6414697292027312262L;

        @XmlElement(name = "Nummer")
        Integer number;

        @XmlElement(name = "Omschrijving")
        @Nullable
        String description;

        @XmlElement(name = "Adres")
        @Nullable
        AddressJaxb address;

        @XmlElement(name = "Contactgegevens")
        @Nullable
        ContactInfoJaxb contactInfo;
    }

    @Getter
    private static class HigherEducationJaxb implements HigherEducation, Serializable {

        @Serial
        private static final long serialVersionUID = -344798218191945921L;

        @XmlElement(name = "Academiejaar")
        @XmlJavaTypeAdapter(YearXmlAdapter.class)
        Year academyYear;

        @XmlElement(name = "Status")
        CodeAndDescriptionJaxb status;

        @XmlElement(name = "DatumInschrijving")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate dateOfEnrollment;

        @XmlElement(name = "DatumUitschrijving")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @Nullable
        LocalDate dateOfDisenrollment;

        @XmlElement(name = "Opleiding")
        @Nullable
        TrainingJaxb training;

        @XmlElement(name = "SoortContract")
        CodeAndDescriptionJaxb contractType;

        @XmlElement(name = "Studiepunten")
        CreditPointsJaxb creditPoints;

        @XmlElement(name = "Studieomvang")
        @Nullable
        Integer studyLoad;

        @XmlElement(name = "ToereikendeStudieomvang")
        Boolean isAdequateStudyLoad;

        @XmlElement(name = "StudiebewijsHaalbaar")
        Boolean isStudyCertificateAchievable;

        @XmlElementWrapper(name = "Studiebewijzen")
        @XmlElement(name = "Studiebewijs")
        @Nullable
        ArrayList<StudyCertificateJaxb> studyCertificates;

        @Override
        public List<StudyCertificate> studyCertificates() {
            if(studyCertificates == null) {
                return null;
            }

            return studyCertificates.stream()
                    .map(x -> (StudyCertificate) x)
                    .toList();
        }

        @XmlElement(name = "VolgendeOpleiding")
        @Nullable
        TrainingJaxb nextTraining;

        @XmlElement(name = "VorigeOpleiding")
        @Nullable
        TrainingJaxb previousTraining;
    }

    @Getter
    private static class CompulsoryEducationJaxb implements CompulsoryEducation, Serializable {

        @Serial
        private static final long serialVersionUID = 394155513929130862L;

        @XmlElement(name = "SoortProgramma")
        CodeAndDescriptionJaxb programType;

        @XmlElement(name = "DatumInschrijving")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @Nullable
        LocalDate dateOfEnrollment;

        @XmlElement(name = "DatumEersteLes")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @Nullable
        LocalDate dateOfFirstClass;

        @XmlElement(name = "Schooljaar")
        @XmlJavaTypeAdapter(YearXmlAdapter.class)
        Year schoolYear;

        @XmlElement(name = "InschrijvingAdministratieveGroep")
        @Nullable
        AdministrativeGroupRegistrationJaxb administrativeGroupRegistration;
    }

    @Getter
    private static class TrainingJaxb implements Training, Serializable {

        @Serial
        private static final long serialVersionUID = 3947632101949342933L;

        @XmlElement(name = "Code")
        String code;

        @XmlElement(name = "VolledigeNaam")
        String fullName;

        @XmlElement(name = "NaamElementen")
        NameElementsJaxb nameElements;

        @XmlElement(name = "Soort")
        CodeAndDescriptionJaxb type;
    }

    @Getter
    private static class NameElementsJaxb implements NameElements, Serializable {

        @Serial
        private static final long serialVersionUID = -7146590833317862189L;

        @XmlElement(name = "PrefixGraad")
        @Nullable
        String degreePrefix;

        @XmlElement(name = "Graad")
        @Nullable
        String degree;

        @XmlElement(name = "SpecificatieGraad")
        @Nullable
        String degreeSpecification;

        @XmlElement(name = "PrefixKwalificatie")
        @Nullable
        String qualificationPrefix;

        @XmlElement(name = "Kwalificatie")
        String qualification;
    }

    @Getter
    private static class CreditPointsJaxb implements CreditPoints, Serializable {

        @Serial
        private static final long serialVersionUID = 4288678212016253460L;

        @XmlElement(name = "Opgenomen")
        Integer amountTaken;

        @XmlElement(name = "Verworven")
        Integer amountAcquired;

        @XmlElement(name = "Gedelibereerd")
        Integer amountDeliberated;

        @XmlElement(name = "GeenResultaat")
        Integer amountNoResult;

        @XmlElement(name = "NietDeelgenomen")
        @Nullable
        Integer amountNotParticipated;

        @XmlElement(name = "NietGeslaagd")
        @Nullable
        Integer amountNotSucceeded;

        @XmlElement(name = "NietVerworven")
        Integer amountNotAcquired;

        @XmlElement(name = "Vrijgesteld")
        Integer amountExempted;

        @XmlElement(name = "Uitgeschreven")
        Integer amountOptedOut;
    }

    @Getter
    private static class StudyCertificateJaxb implements StudyCertificate, Serializable {

        @Serial
        private static final long serialVersionUID = -7569422110911431585L;

        @XmlElement(name = "Referte")
        String reference;

        @XmlElement(name = "LEDReferte")
        @Nullable
        String ledReference;
    }

    @Getter
    private static class AdministrativeGroupRegistrationJaxb implements AdministrativeGroupRegistration, Serializable {

        @Serial
        private static final long serialVersionUID = -4857829255909214297L;

        @XmlElement(name = "Referte")
        @Nullable
        String reference;

        @XmlElement(name = "Periode")
        PeriodJaxb period;

        @XmlElement(name = "AdministratieveGroep")
        AdministrativeGroupJaxb administrativeGroup;

        @XmlElement(name = "TypeBuitengewoon")
        @Nullable
        CodeAndDescriptionJaxb specialNeedsType;

        @XmlElement(name = "Uitschrijving")
        @Nullable
        DisenrollmentJaxb disenrollment;

        @XmlElement(name = "AnderstaligeNieuwkomer")
        @Nullable
        Boolean isForeignLanguageNewcomer;

        @XmlElement(name = "GewezenAnderstaligeNieuwkomer")
        @Nullable
        Boolean isFormerForeignLanguageNewcomer;
    }

    @Getter
    private static class AdministrativeGroupJaxb implements AdministrativeGroup, Serializable {

        @Serial
        private static final long serialVersionUID = -2950123647627117385L;

        @XmlElement(name = "Nummer")
        String number;

        @XmlElement(name = "Naam")
        @Nullable
        String name;

        @XmlElement(name = "Niveau")
        CodeAndDescriptionJaxb level;

        @XmlElement(name = "SoortOnderwijs")
        CodeAndDescriptionJaxb educationType;

        @XmlElement(name = "DuaalLeren")
        @Nullable
        DualCurriculumJaxb dualCurriculum;
    }

    @Getter
    private static class DualCurriculumJaxb implements DualCurriculum, Serializable {

        @Serial
        private static final long serialVersionUID = -4011341482386475604L;

        @XmlElement(name = "Leerweg")
        CodeAndDescriptionJaxb learningPathway;
    }

    @Getter
    private static class DisenrollmentJaxb implements Disenrollment, Serializable {

        @Serial
        private static final long serialVersionUID = -990045071616004454L;

        @XmlElement(name = "Aanleiding")
        CodeAndDescriptionJaxb reason;

        @XmlElement(name = "SituatieNa")
        CodeAndDescriptionJaxb situationAfter;
    }

    @Getter
    private static class AddressJaxb implements Address, Serializable {

        @Serial
        private static final long serialVersionUID = -2602789444589500341L;

        @XmlElement(name = "Straat")
        @Nullable
        StreetJaxb street;

        @XmlElement(name = "Huisnummer")
        @Nullable
        String houseNumber;

        @XmlElement(name = "Busnummer")
        @Nullable
        String postalBoxNumber;

        @XmlElement(name = "Gemeente")
        @Nullable
        MunicipalityJaxb municipality;

        @XmlElement(name = "Land")
        @Nullable
        CountryJaxb country;
    }

    @Getter
    private static class StreetJaxb implements Street, Serializable {

        @Serial
        private static final long serialVersionUID = -8500375798881388985L;

        @XmlElement(name = "Naam")
        @Nullable
        String name;
    }

    @Getter
    private static class MunicipalityJaxb implements Municipality, Serializable {

        @Serial
        private static final long serialVersionUID = -5732523182095981641L;

        @XmlElement(name = "NISCode")
        @Nullable
        String nisCode;

        @XmlElement(name = "PostCode")
        @Nullable
        String postalCode;

        @XmlElement(name = "Naam")
        @Nullable
        String name;
    }

    @Getter
    private static class CountryJaxb implements Country, Serializable {

        @Serial
        private static final long serialVersionUID = 3027740616814131639L;

        @XmlElement(name = "ISOCode")
        @Nullable
        String isoCode;

        @XmlElement(name = "Naam")
        @Nullable
        String name;
    }

    @Getter
    private static class ContactInfoJaxb implements ContactInfo, Serializable {

        @Serial
        private static final long serialVersionUID = 4043991542075763020L;

        @XmlElement(name = "Telefoonnummer")
        @Nullable
        String phoneNumber;

        @XmlElement(name = "Email")
        @Nullable
        String emailAddress;

        @XmlElement(name = "Website")
        @Nullable
        String website;
    }

    @Getter
    private static class PeriodJaxb implements Period, Serializable {

        @Serial
        private static final long serialVersionUID = -7829462301916655547L;

        @XmlElement(name = "Begin")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate startDate;

        @XmlElement(name = "Einde")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate endDate;
    }

    @Getter
    private static class CodeAndDescriptionJaxb implements CodeAndDescription, Serializable {

        @Serial
        private static final long serialVersionUID = -5682187439322660274L;

        @XmlElement(name = "Code")
        String codeValue;

        @XmlElement(name = "Omschrijving")
        @Nullable
        String codeDescription;
    }
}
