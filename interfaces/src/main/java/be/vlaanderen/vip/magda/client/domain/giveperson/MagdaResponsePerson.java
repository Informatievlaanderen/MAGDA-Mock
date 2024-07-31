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
    }

    private static class NodeDetailedRelatedPerson extends NodeRelatedPerson implements DetailedRelatedPerson {

        public NodeDetailedRelatedPerson(Node node) {
            super(node);
        }

        @Override
        public IncompleteDate incompleteDateOfBirth() {
            return node.get("Geboorte/Datum")
                    .flatMap(Node::getValue)
                    .map(IncompleteDate::fromString)
                    .orElse(null);
        }
        
        @Override
        public Optional<LocalDate> deathDate() {
            return node.get("Overlijden/Datum")
                       .flatMap(Node::getValue)
                       .map(LocalDate::parse);
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

        private String standardizedStreet() {
            return node.get("Straatnaam/String")
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

        private String standardizedMunicipality() {
            return node.get("Gemeentenaam/String")
                    .flatMap(Node::getValue)
                    .orElse(null);
        }
    }
}