package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.MalformedMagdaResponseException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.dto.IncompleteDate;
import be.vlaanderen.vip.magda.client.xml.node.Node;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record MagdaResponsePerson(MagdaResponseWrapper response) implements Person {

    @Override
    public DetailedRelatedPerson self() {
        return response.getNode("//Persoon")
                       .map(NodeDetailedRelatedPerson::new)
                       .orElseThrow(() -> new MalformedMagdaResponseException("Magda response document misses an expected 'Persoon' node"));
    }

    @Override
    public Optional<RelatedPerson> referencePerson() {
        return response.getNode("//Persoon/Referentiepersoon")
                       .map(NodeRelatedPerson::new);
    }

    @Override
    public Optional<FamilyMemberPerson> referenceFamilyMember() {
        return response.getNode("//Persoon/Referentiepersoon")
                       .map(NodeFamilyMemberPerson::new);
    }

    @Override
    public List<RelatedPerson> familyMembers() {
        return familyMemberNodes().<RelatedPerson>map(NodeRelatedPerson::new)
                                  .toList();
    }

    @Override
    public Optional<RelatedPerson> partner() {
        return findFamilyMembers(this::hasPartnerCode)
                .findFirst();
    }

    @Override
    public List<RelatedPerson> unrelatedResidents() {
        return findFamilyMembers(this::hasNotRelatedCode)
                .toList();
    }
    
    private Stream<RelatedPerson> findFamilyMembers(Predicate<Node> filter) {
        return familyMemberNodes().filter(filter)
                                  .map(NodeRelatedPerson::new);
    }
    
    private Stream<Node> familyMemberNodes() {
        return response.getNodes("//Gezinsleden/Gezinslid");
    }

    private boolean hasPartnerCode(Node familyMemberNode) {
        return familyMemberHasCode(familyMemberNode, "02", "21", "22");
    }
    
    private boolean hasNotRelatedCode(Node familyMemberNode) {
        return familyMemberHasCode(familyMemberNode, "12");
    }
    
    private boolean familyMemberHasCode(Node familyMemberNode, String...codes) {
        return familyMemberNode.get("Positie/Code")
                               .flatMap(Node::getValue)
                               .map(code -> Arrays.asList(codes)
                                                  .contains(code))
                               .orElse(false);
    }
    
    private static class NodeRelatedPerson implements RelatedPerson {

        protected final Node node;

        public NodeRelatedPerson(Node node) {
            this.node = node;
        }

        @Override
        public String insz() {
            return inszOptional()
                    .orElseThrow(() -> new MalformedMagdaResponseException("Magda response document misses an expected 'INSZ' node"));
        }

        @Override
        public Optional<String> inszOptional() {
            return node.get("INSZ")
                    .flatMap(Node::getValue);
        }

        @Override
        public String firstName() {
            return node.get("Naam/Voornamen/Voornaam")
                       .stream()
                       .flatMap(Node::stream)
                       .map(Node::getValue)
                       .flatMap(Optional::stream)
                       .collect(Collectors.joining(" "));
        }
        
        @Override
        public String lastName() {
            return node.get("Naam/Achternamen/Achternaam")
                       .stream()
                       .flatMap(Node::stream)
                       .map(Node::getValue)
                       .flatMap(Optional::stream)
                       .collect(Collectors.joining(" "));
        }

        @Override
        public String positionCode() {
            return node.get("Positie/Code")
                    .flatMap(Node::getValue)
                    .orElseThrow(() -> new MalformedMagdaResponseException("Magda response document misses an expected 'Positie/Code' node"));
        }

        @Override
        public IncompleteDate incompleteDateOfBirth() {
            return node.get("Geboorte/Datum")
                    .or(() -> node.get("Geboortedatum"))
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString)
                    .orElse(null);
        }
    }

    private static class NodeDetailedRelatedPerson extends NodeRelatedPerson implements DetailedRelatedPerson {

        public NodeDetailedRelatedPerson(Node node) {
            super(node);
        }
        
        @Override
        @Deprecated(forRemoval = true)
        public Optional<LocalDate> deathDate() {
            return node.get("Overlijden/Datum")
                       .flatMap(Node::getValue)
                       .map(LocalDate::parse);
        }

        @Override
        public Optional<IncompleteDate> incompleteDateOfDeath() {
            return node.get("Overlijden/Datum")
                       .flatMap(Node::getValue)
                       .map(IncompleteDate::fromString);
        }


        @Override
        public Address mainResidence() {
            return node.get("Adressen/Hoofdverblijfplaats")
                    .map(NodeAddress::new)
                    .orElse(null);
        }

        @Override
        public Address contactAddress() {
            return node.get("Adressen/ContactAdres")
                    .map(NodeAddress::new)
                    .orElse(null);
        }

        @Override
        public LegalHabidationAddress legalHabidationAddress() {
            return node.get("Adressen/WettelijkeWoonplaats")
                    .map(NodeLegalHabidationAddress::new)
                    .orElse(null);
        }

        @Override
        public SpecifiedAddress specifiedAddress() {
            return node.get("Adressen/OpgegevenAdres")
                    .map(NodeSpecifiedAddress::new)
                    .orElse(null);
        }
    }

    private static class NodeFamilyMemberPerson extends NodeRelatedPerson implements FamilyMemberPerson {

        public NodeFamilyMemberPerson(Node node) {
            super(node);
        }

        @Override
        public Optional<LocalDate> startDate() {
            return node.get("@DatumBegin")
                    .flatMap(Node::getValue)
                    .map(LocalDate::parse);
        }

        @Override
        public Optional<LocalDate> endDate() {
            return node.get("@DatumEinde")
                    .flatMap(Node::getValue)
                    .map(LocalDate::parse);
        }
    }

    private record NodeAddress(Node node) implements Address {

        @Override
        public Optional<IncompleteDate> startDate() {
            return node.get("@DatumBegin")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public Optional<IncompleteDate> endDate() {
            return node.get("@DatumEinde")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public String street() {
            return node.get("NietGestandaardiseerdeStraatnaam/String")
                    .flatMap(Node::getValue)
                    .orElseGet(this::standardizedStreet);
        }

        @Override
        public String streetLang() {
            return node.get("NietGestandaardiseerdeStraatnaam/Taal")
                    .flatMap(Node::getValue)
                    .orElseGet(this::standardizedStreetLang);
        }

        private String standardizedStreet() {
            return node.get("Straatnaam/String")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        private String standardizedStreetLang() {
            return node.get("Straatnaam/Taal")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String houseNumber() {
            return node.get("Huisnummer")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public Optional<String> postalBoxNumber() {
            return node.get("Busnummer")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> nisCode() {
            return node.get("NISCodeGemeente")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> zipCode() {
            return node.get("Postcode")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> municipality() {
            return Optional.ofNullable(node.get("NietGestandaardiseerdeGemeentenaam/String")
                    .flatMap(Node::getValue)
                    .orElseGet(this::standardizedMunicipality));
        }

        @Override
        public Optional<String> municipalityLang() {
            return Optional.ofNullable(node.get("NietGestandaardiseerdeGemeentenaam/Taal")
                    .flatMap(Node::getValue)
                    .orElseGet(this::standardizedMunicipalityLang));
        }

        @Override
        public Optional<String> nisCodeCountry() {
            return node.get("NISCodeLand")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> isoCodeCountry() {
            return node.get("ISOCodeLand")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> countryName() {
            return node.get("Land/String")
                    .flatMap(Node::getValue);
        }

        @Override
        public Optional<String> countryNameLang() {
            return node.get("Land/Taal")
                    .flatMap(Node::getValue);
        }

        private String standardizedMunicipality() {
            return node.get("Gemeentenaam/String")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        private String standardizedMunicipalityLang() {
            return node.get("Gemeentenaam/Taal")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }
    }

    private record NodeLegalHabidationAddress(Node node) implements LegalHabidationAddress {
        @Override
        public Optional<IncompleteDate> startDate() {
            return node.get("@DatumBegin")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public Optional<IncompleteDate> endDate() {
            return node.get("@DatumEinde")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public String text() {
            return node.get("Tekst")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

    }

    private record NodeSpecifiedAddress(Node node) implements SpecifiedAddress {

        @Override
        public Optional<IncompleteDate> startDate() {
            return node.get("@DatumBegin")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public Optional<IncompleteDate> endDate() {
            return node.get("@DatumEinde")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString);
        }

        @Override
        public LanguageString fullAddress() {
            return node.get("VolledigAdres")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public String postBus() {
            return node.get("Postbus")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public LanguageString streetName() {
            return node.get("Straatnaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString nonStandardizedStreetName() {
            return node.get("NietGestandaardiseerdeStraatnaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public String streetCode() {
            return node.get("Straatcode")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String locationIndication() {
            return node.get("Locatieaanduiding")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String houseNumber() {
            return node.get("Huisnummer")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public Optional<String> postalBoxNumber() {
            return node.get("Busnummer").flatMap(Node::getValue);
        }

        @Override
        public LanguageString locationName() {
            return node.get("Locatienaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString addressRegion() {
            return node.get("Adresgebied")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString postalName() {
            return node.get("Postnaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString municipalityName() {
            return node.get("Gemeentenaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString nonStandardizedMunicipalityName() {
            return node.get("NietGestandaardiseerdeGemeentenaam")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public Optional<String> nisCode() {
            return node.get("NISCodeGemeente").flatMap(Node::getValue);
        }

        @Override
        public Optional<String> zipCode() {
            return node.get("PostCode").flatMap(Node::getValue);
        }

        @Override
        public LanguageString administrativeUnitLevel1() {
            return node.get("AdministratieveEenheidNiveau1")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString administrativeUnitLevel2() {
            return node.get("AdministratieveEenheidNiveau2")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public LanguageString country() {
            return node.get("Land")
                    .map(NodeLanguageString::new)
                    .orElse(null);
        }

        @Override
        public Optional<String> nisCodeCountry() {
            return node.get("NisCodeLand").flatMap(Node::getValue);
        }

        @Override
        public Optional<String> isoCodeCountry() {
            return node.get("ISOCodeLand").flatMap(Node::getValue);
        }

        @Override
        public BeStAddId refersTo() {
            return node.get("VerwijstNaar")
                    .map(NodeBeStAddId::new)
                    .orElse(null);
        }

        @Override
        public BeStAddId municipalityBeStAddId() {
            return node.get("GemeenteBeStAddId")
                    .map(NodeBeStAddId::new)
                    .orElse(null);
        }

        @Override
        public BeStAddId streetBeStAddId() {
            return node.get("StraatBeStAddId")
                    .map(NodeBeStAddId::new)
                    .orElse(null);
        }
    }

    private record NodeLanguageString(Node node) implements LanguageString {

        @Override
        public String value() {
            return node.get("String")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String language() {
            return node.get("Taal")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }
    }

    private record NodeBeStAddId(Node node) implements BeStAddId {

        @Override
        public String localIdentificator() {
            return node.get("LokaleIdentificator")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String namespace() {
            return node.get("Naamruimte")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }

        @Override
        public String versionIdentificator() {
            return node.get("VersieIdentificator")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }
    }
}