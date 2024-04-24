package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.MalformedMagdaResponseException;
import be.vlaanderen.vip.magda.client.domain.dto.INSZ;
import be.vlaanderen.vip.magda.client.domain.dto.IncompleteDateMissingPartException;
import be.vlaanderen.vip.magda.client.domain.dto.IncompleteDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Personal information on the relationships of a citizen.
 */
public interface Person {

    /**
     * Information on the citizen themselves.
     */
    DetailedRelatedPerson self();

    /**
     * Information on the reference person of the citizen (if any).
     */
    Optional<RelatedPerson> referencePerson();

    /**
     * Information on the reference person (as family member) of the citizen (if any).
     */
    Optional<FamilyMemberPerson> referenceFamilyMember();

    /**
     * Information on the family members of the citizen.
     */
    List<RelatedPerson> familyMembers();

    /**
     * Information on the partner of the citizen (if any).
     */
    Optional<RelatedPerson> partner();

    /**
     * Information on the familially unrelated persons who are co-residents of the citizen.
     */
    List<RelatedPerson> unrelatedResidents();

    /**
     * Basic personal information.
     */
    interface RelatedPerson {

        /**
         * INSZ number of the person.
         *
         * @throws MalformedMagdaResponseException if the field is not present.
         * @see INSZ
         * @deprecated This field is in fact not always present in a well-formed document; use {@link #inszOptional()} as a replacement for this method.
         */
        @Deprecated
        String insz();

        /**
         * INSZ number of the person (if any).
         *
         * @see INSZ
         */
        Optional<String> inszOptional();

        /**
         * The first name of the person.
         *
         * @return concatenation of the person's first names, or an empty string if the document contains no information on their first names.
         */
        String firstName();

        /**
         * The last name of the person.
         *
         * @return concatenation of the person's last names, or an empty string if the document contains no information on their last names.
         */
        String lastName();
    }

    /**
     * Detailed personal information.
     */
    interface DetailedRelatedPerson extends RelatedPerson {

        /**
         * The person's date of birth.
         *
         * @throws java.time.format.DateTimeParseException in case it's an incomplete date.
         * @deprecated since this date is possibly incomplete, use {@link #incompleteDateOfBirth()} and call {@link IncompleteDate#toLocalDate()} to extract the actual date. Make sure to handle the {@link IncompleteDateMissingPartException} that may be thrown by that.
         */
        @Deprecated(forRemoval = true)
        LocalDate dateOfBirth();

        /**
         * The person's (potentially incomplete) date of birth.
         */
        IncompleteDate incompleteDateOfBirth();

        /**
         * The person's date of death (if any).
         *
         * @throws java.time.format.DateTimeParseException in case it's an incomplete date.
         */
        Optional<LocalDate> deathDate();

        /**
         * The address of the person's main residence.
         */
        Address mainResidence();
    }

    /**
     * Information on a person's family member.
     */
    interface FamilyMemberPerson extends RelatedPerson {

        /**
         * The date when this related person started being a family member of the person.
         * This field should in principle always be present, but it's technically possible for it to be missing from a document.
         *
         * @throws java.time.format.DateTimeParseException in case it's an incomplete date.
         */
        Optional<LocalDate> startDate();

        /**
         * The date when this related person ceased to be a family member of the person.
         * If there is no end date, then they're still a family member of the person to this day.
         *
         * @throws java.time.format.DateTimeParseException in case it's an incomplete date.
         */
        Optional<LocalDate> endDate();
    }

    /**
     * Address information.
     */
    interface Address {

        /**
         * Street name.
         */
        String street();

        /**
         * House number.
         */
        String houseNumber();

        /**
         * PO box number.
         */
        Optional<String> postalBoxNumber();

        /**
         * NIS code; a numeric code for regional areas in Belgium.
         *
         * @see <a href="https://statbel.fgov.be/sites/default/files/Over_Statbel_FR/Nomenclaturen/REFNIS_2019.csv">List of NIS codes</a>
         */
        Optional<String> nisCode();

        /**
         * Postal or ZIP code of the region.
         */
        Optional<String> zipCode();

        /**
         * Name of the municipality.
         */
        Optional<String> municipality();

        Optional<String> nisCodeCountry();

        Optional<String> isoCodeCountry();

    }
}
