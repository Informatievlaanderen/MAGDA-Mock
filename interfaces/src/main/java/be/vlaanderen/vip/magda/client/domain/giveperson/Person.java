package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.domain.dto.INSZ;

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
         * @see INSZ
         */
        String insz();

        /**
         * The first name of the person.
         */
        String firstName();

        /**
         * The last name of the person.
         */
        String lastName();
    }

    /**
     * Detailed personal information.
     */
    interface DetailedRelatedPerson extends RelatedPerson {

        /**
         * The person's date of birth.
         */
        LocalDate dateOfBirth();

        /**
         * The person's date of death (if any).
         */
        Optional<LocalDate> deathDate();

        /**
         * The address of the person's main residence.
         */
        Address mainResidence();
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

    }
}
