package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Information on a citizen's history of enrollment in education.
 */
public interface EnrollmentHistory {

    static EnrollmentHistory ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return MagdaResponseEnrollmentHistoryAdapterJaxbImpl.getInstance().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    List<Enrollment> enrollments();

    interface Enrollment {

        EducationEnrollmentSource source();

        String reference();

        Institution institution();

        @Nullable
        EducationLocation educationLocation();

        @Nullable
        HigherEducation higherEducation();

        @Nullable
        CompulsoryEducation compulsoryEducation();
    }

    interface Institution {

        String institutionNumber();

        String officialName();

        @Nullable
        String agency();

        @Nullable
        Address address();

        @Nullable
        ContactInfo contactInfo();
    }

    interface EducationLocation {

        Integer number();

        @Nullable
        String description();

        @Nullable
        Address address();

        @Nullable
        ContactInfo contactInfo();
    }

    interface HigherEducation {

        Year academyYear();

        CodeAndDescription status();

        LocalDate dateOfEnrollment();

        @Nullable
        LocalDate dateOfDisenrollment();

        @Nullable
        Training training();

        CodeAndDescription contractType();

        CreditPoints creditPoints();

        @Nullable
        Integer studyLoad();

        Boolean isAdequateStudyLoad();

        Boolean isStudyCertificateAchievable();

        @Nullable
        List<StudyCertificate> studyCertificates();

        @Nullable
        Training nextTraining();

        @Nullable
        Training previousTraining();
    }

    interface CompulsoryEducation {

        CodeAndDescription programType();

        @Nullable
        LocalDate dateOfEnrollment();

        @Nullable
        LocalDate dateOfFirstClass();

        Year schoolYear();

        @Nullable
        AdministrativeGroupRegistration administrativeGroupRegistration();
    }

    interface Training {

        String code();

        String fullName();

        NameElements nameElements();

        CodeAndDescription type();
    }

    interface NameElements {

        @Nullable
        String degreePrefix();

        @Nullable
        String degree();

        @Nullable
        String degreeSpecification();

        @Nullable
        String qualificationPrefix();

        String qualification();
    }

    interface CreditPoints {

        Integer amountTaken();

        Integer amountAcquired();

        Integer amountDeliberated();

        Integer amountNoResult();

        @Nullable
        Integer amountNotParticipated();

        @Nullable
        Integer amountNotSucceeded();

        Integer amountNotAcquired();

        Integer amountExempted();

        Integer amountOptedOut();
    }

    interface StudyCertificate {

        String reference();

        @Nullable
        String ledReference();
    }

    interface AdministrativeGroupRegistration {

        @Nullable
        String reference();

        Period period();

        AdministrativeGroup administrativeGroup();

        @Nullable
        CodeAndDescription specialNeedsType();

        @Nullable
        Disenrollment disenrollment();

        @Nullable
        Boolean isForeignLanguageNewcomer();

        @Nullable
        Boolean isFormerForeignLanguageNewcomer();
    }

    interface AdministrativeGroup {

        String number();

        @Nullable
        String name();

        CodeAndDescription level();

        CodeAndDescription educationType();

        @Nullable
        DualCurriculum dualCurriculum();
    }

    interface DualCurriculum {

        CodeAndDescription learningPathway();
    }

    interface Disenrollment {

        CodeAndDescription reason();

        CodeAndDescription situationAfter();
    }

    interface Address {

        @Nullable
        Street street();

        @Nullable
        String houseNumber();

        @Nullable
        String postalBoxNumber();

        @Nullable
        Municipality municipality();

        @Nullable
        Country country();
    }

    interface Street {

        @Nullable
        String name();
    }

    interface Municipality {

        @Nullable
        String nisCode();

        @Nullable
        String postalCode();

        @Nullable
        String name();
    }

    interface Country {

        @Nullable
        String isoCode();

        @Nullable
        String name();
    }

    interface ContactInfo {

        @Nullable
        String phoneNumber();

        @Nullable
        String emailAddress();

        @Nullable
        String website();
    }

    interface Period {

        LocalDate startDate();

        LocalDate endDate();
    }

    interface CodeAndDescription {

        String codeValue();

        @Nullable
        String codeDescription();
    }
}