package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.domain.dto.PartialDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @deprecated This class is out of scope for MagdaMock and is to be removed from it.
 */
@Deprecated(forRemoval = true)
public record PersonRecord(
        DetailedRelatedPersonRecord selfRecord,
        RelatedPersonRecord referencePersonRecord,
        List<RelatedPersonRecord> familyMembersRecord,
        RelatedPersonRecord partnerRecord,
        List<RelatedPersonRecord> unrelatedResidentsRecord) implements Person, Serializable {

    @Override
    public DetailedRelatedPerson self() {
        return selfRecord;
    }

    @Override
    public Optional<RelatedPerson> referencePerson() {
        return Optional.ofNullable(referencePersonRecord)
                       .map(RelatedPerson.class::cast);
    }

    @Override
    public Optional<FamilyMemberPerson> referenceFamilyMember() {
        return Optional.ofNullable(referencePersonRecord)
                       .map(FamilyMemberPerson.class::cast);
    }

    @Override
    public List<RelatedPerson> familyMembers() {
        return familyMembersRecord.stream()
                                  .map(RelatedPerson.class::cast)
                                  .toList();
    }

    @Override
    public Optional<RelatedPerson> partner() {
        return Optional.ofNullable(partnerRecord)
                       .map(RelatedPerson.class::cast);
    }

    @Override
    public List<RelatedPerson> unrelatedResidents() {
        return unrelatedResidentsRecord.stream()
                                     .map(RelatedPerson.class::cast)
                                     .toList();
    }
    
    
    public static PersonRecord from(Person person) {
        return new PersonRecord(DetailedRelatedPersonRecord.from(person.self()),
                                person.referencePerson().map(RelatedPersonRecord::from).orElse(null),
                                person.familyMembers().stream().map(RelatedPersonRecord::from).toList(),
                                person.partner().map(RelatedPersonRecord::from).orElse(null),
                                person.unrelatedResidents().stream().map(RelatedPersonRecord::from).toList());
    }

    public record RelatedPersonRecord(
            String insz,
            String firstName,
            String lastName) implements FamilyMemberPerson, Serializable {
        
        public static RelatedPersonRecord from(RelatedPerson person) {
            return new RelatedPersonRecord(person.insz(),
                                           person.firstName(),
                                           person.lastName());
        }

        @Override
        public Optional<String> inszOptional() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<LocalDate> startDate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<LocalDate> endDate() {
            throw new UnsupportedOperationException();
        }
    }
    
    public record DetailedRelatedPersonRecord(
            String insz,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            LocalDate deathDateValue,
            AddressRecord mainResidence) implements DetailedRelatedPerson, Serializable {

        public Optional<LocalDate> deathDate() {
            return Optional.ofNullable(deathDateValue);
        }
        
        public static DetailedRelatedPersonRecord from(DetailedRelatedPerson person) {
            return new DetailedRelatedPersonRecord(person.insz(),
                                                   person.firstName(),
                                                   person.lastName(),
                                                   person.dateOfBirth(),
                                                   person.deathDate().orElse(null),
                                                   AddressRecord.from(person.mainResidence()));
        }

        @Override
        public Optional<String> inszOptional() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PartialDate partialDateOfBirth() {
            throw new UnsupportedOperationException();
        }
    }
    
    public record AddressRecord(
            String street,
            String houseNumber,
            String postalBoxNumberValue,
            String nisCodeValue,
            String zipCodeValue,
            String municipalityValue) implements Address, Serializable {

        @Override
        public Optional<String> postalBoxNumber() {
            return Optional.ofNullable(postalBoxNumberValue);
        }

        @Override
        public Optional<String> nisCode() {
            return Optional.ofNullable(nisCodeValue);
        }

        @Override
        public Optional<String> zipCode() {
            return Optional.ofNullable(zipCodeValue);
        }

        @Override
        public Optional<String> municipality() {
            return Optional.ofNullable(municipalityValue);
        }
        
        public static AddressRecord from(Address address) {
            return new AddressRecord(address.street(),
                                     address.houseNumber(),
                                     address.postalBoxNumber().orElse(null),
                                     address.nisCode().orElse(null),
                                     address.zipCode().orElse(null),
                                     address.municipality().orElse(null));
        }
        
    }
}
