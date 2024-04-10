package be.vlaanderen.vip.mock.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.MalformedMagdaResponseException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.giveperson.MagdaResponsePerson;
import be.vlaanderen.vip.magda.client.domain.giveperson.Person;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static be.vlaanderen.vip.magda.client.domain.giveperson.Person.*;
import static be.vlaanderen.vip.magda.client.domain.giveperson.Person.DetailedRelatedPerson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class MagdaResponsePersonIntegrationTest {

    // TODO needs some tests around the behavior of parsing incomplete dates (expect DateTimeParseExceptions)

    @Nested
    class Self {
        private DetailedRelatedPerson self;

        @BeforeEach
        void setup() {
            self = person("99722099716").self();
        }

        @Test
        void mapsInsz() {
            assertThat(self.insz(), is(equalTo("99722099716")));
        }

        @Test
        void mapsInszOptional() {
            assertThat(self.inszOptional(), is(equalTo(Optional.of("99722099716"))));
        }

        @Test
        void mapsFirstName() {
            assertThat(self.firstName(), is(equalTo("Orelio Rosario Maurice")));
        }

        @Test
        void mapsLastName() {
            assertThat(self.lastName(), is(equalTo("Janssens")));
        }

        @Test
        void mapsDateOfBirth() {
            assertThat(self.dateOfBirth(), is(equalTo(LocalDate.of(2000, 12, 20))));
        }
        
        @Test
        void deathDateNull_whenMissing() {
            assertThat(self.deathDate().isEmpty(), is(true));
        }
        
        @Test
        void mapsDeathDate() {
            MatcherAssert.assertThat(person("65712399877").self().deathDate().get(), is(equalTo(LocalDate.of(2002, 1, 1))));
        }

        @Test
        void mapsMainResidence_caseA() {
            var address = person("78721626143").self().mainResidence();

            assertNotNull(address);
            Assertions.assertEquals("Rue de la Station", address.street());
            Assertions.assertEquals("13", address.houseNumber());
            Assertions.assertEquals(Optional.of("2.02"), address.postalBoxNumber());
            Assertions.assertEquals(Optional.of("52011"), address.nisCode());
            Assertions.assertEquals(Optional.of("6043"), address.zipCode());
            Assertions.assertEquals(Optional.of("Charleroi"), address.municipality());
        }

        @Test
        void mapsMainResidence_caseB() {
            var address = self.mainResidence();

            assertNotNull(address);
            assertEquals("Dorpsstraat", address.street());
            assertEquals("405", address.houseNumber());
            assertEquals(Optional.empty(), address.postalBoxNumber());
            assertEquals(Optional.of("24055"), address.nisCode());
            assertEquals(Optional.of("2340"), address.zipCode());
            assertEquals(Optional.of("Kortenberg"), address.municipality());
        }
    }

    @Nested
    class ReferencePerson {
        private RelatedPerson referencePerson;

        @BeforeEach
        void setup() {
            var optionalReferencePerson = person("99722099716").referencePerson();
            assert optionalReferencePerson.isPresent();

            referencePerson = optionalReferencePerson.get();
        }
        
        @Test
        void mapsInsz() {
            assertThat(referencePerson.insz(), is(equalTo("75652299881")));
        }

        @Test
        void mapsInszOptional() {
            assertThat(referencePerson.inszOptional(), is(equalTo(Optional.of("75652299881"))));
        }
        
        @Test
        void mapsFirstName() {
            assertThat(referencePerson.firstName(), is(equalTo("Catherine")));
        }
        
        @Test
        void mapsLastName() {
            assertThat(referencePerson.lastName(), is(equalTo("Dubois")));
        }

        @Test
        void emptyWhenNoReferencePerson() {
            Assertions.assertTrue(person("00630300188").referencePerson().isEmpty());
        }
    }

    @Nested
    class ReferenceFamilyMember {
        private FamilyMemberPerson referenceFamilyMemberA;

        @BeforeEach
        void setup() {
            var optionalReferenceFamilyMemberA = person("99722099716").referenceFamilyMember();
            assert optionalReferenceFamilyMemberA.isPresent();

            referenceFamilyMemberA = optionalReferenceFamilyMemberA.get();
        }

        @Test
        void mapsInsz() {
            assertThat(referenceFamilyMemberA.insz(), is(equalTo("75652299881")));
        }

        @Test
        void mapsInszOptional() {
            assertThat(referenceFamilyMemberA.inszOptional(), is(equalTo(Optional.of("75652299881"))));
        }

        @Test
        void mapsFirstName() {
            assertThat(referenceFamilyMemberA.firstName(), is(equalTo("Catherine")));
        }

        @Test
        void mapsLastName() {
            assertThat(referenceFamilyMemberA.lastName(), is(equalTo("Dubois")));
        }

        @Test
        void mapsStartDate_withStartDatePresent() {
            assertThat(referenceFamilyMemberA.startDate(), is(equalTo(Optional.of(LocalDate.of(2000, 12, 20)))));
        }

        // FIXME technically, we'd also need a test case where the start date is missing, but the simulator has no XMLs with a missing DatumBegin attribute.

        @Test
        void mapsEndDate_withEndDatePresent() {
            var optionalReferenceFamilyMemberB = person("02680899786").referenceFamilyMember();
            assert optionalReferenceFamilyMemberB.isPresent();

            // this is a case of a reference person with an end date
            var referenceFamilyMemberB = optionalReferenceFamilyMemberB.get();

            assertThat(referenceFamilyMemberB.endDate(), is(Optional.of(LocalDate.of(1994, 4, 28))));
        }

        @Test
        void mapsEndDate_withEndDateAbsent() {
            assertThat(referenceFamilyMemberA.endDate(), is(equalTo(Optional.empty())));
        }

        @Test
        void emptyWhenNoReferencePerson() {
            Assertions.assertTrue(person("00630300188").referencePerson().isEmpty());
        }
    }
    
    @Nested
    class FamilyMembers {
        private RelatedPerson memberA1;
        private RelatedPerson memberA2;
        private RelatedPerson memberB1;

        @BeforeEach
        void setup() {
            var membersA = person("41620999728").familyMembers();
            assertThat(membersA, hasSize(2));

            memberA1 = membersA.get(0);
            memberA2 = membersA.get(1);

            var membersB = person("66691666644").familyMembers();
            assertThat(membersB, hasSize(1));

            // this is a case of a family member with no INSZ, first name or last name information
            memberB1 = membersB.get(0);
        }
        
        @Test
        void mapsInsz_withInszPresent() {
            assertAll(
                    () -> assertThat(memberA1.insz(), is(equalTo("02690399819"))),
                    () -> assertThat(memberA2.insz(), is(equalTo("75721999826"))));
        }

        @Test
        void mapsInsz_withInszPresent_throwsException() {
            assertAll(
                    () -> assertThrows(MalformedMagdaResponseException.class, () -> memberB1.insz()));
        }

        @Test
        void mapsInszOptional_withInszPresent() {
            assertAll(
                    () -> assertThat(memberA1.inszOptional(), is(equalTo(Optional.of("02690399819")))),
                    () -> assertThat(memberA2.inszOptional(), is(equalTo(Optional.of("75721999826")))));
        }

        @Test
        void mapsInszOptional_withInszAbsent() {
            assertAll(
                    () -> assertThat(memberB1.inszOptional(), is(equalTo(Optional.empty()))));
        }
        
        @Test
        void mapsFirstName_withFirstNamesPresent() {
            assertAll(
                    () -> assertThat(memberA1.firstName(), is(equalTo("Siska Luka"))),
                    () -> assertThat(memberA2.firstName(), is(equalTo("Nele Mariette Rolanda"))));
        }

        @Test
        void mapsFirstName_withFirstNamesAbsent_asEmptyString() {
            assertAll(
                    () -> assertThat(memberB1.firstName(), is(equalTo(""))));
        }
        
        @Test
        void mapsLastName_withLastNamesPresent() {
            assertAll(
                    () -> assertThat(memberA1.lastName(), is(equalTo("Janssens"))),
                    () -> assertThat(memberA2.lastName(), is(equalTo("Duchamp"))));
        }

        @Test
        void mapsLastName_withLastNamesAbsent_asEmptyString() {
            assertAll(
                    () -> assertThat(memberB1.lastName(), is(equalTo(""))));
        }
    }
    
    @Nested
    class Partner {
        
        @Test
        void emptyWhenNoPartners() {
            MatcherAssert.assertThat(person("41620999728").partner().isEmpty(), is(true));
        }
        
        @Nested
        class HasPartner {
            private RelatedPerson partner;
            
            @BeforeEach
            void setup() {
                var oPartner = person("05620999678").partner();
                
                MatcherAssert.assertThat(oPartner.isPresent(), is(true));
                
                partner = oPartner.get();
            }
            
            @Test
            void mapsInsz() {
                assertThat(partner.insz(), is(equalTo("75622399830")));
            }

            @Test
            void mapsInszOptional() {
                assertThat(partner.inszOptional(), is(equalTo(Optional.of("75622399830"))));
            }
            
            @Test
            void mapsFirstName() {
                assertThat(partner.firstName(), is(equalTo("Gabriela")));
            }
            
            @Test
            void mapsLastName() {
                assertThat(partner.lastName(), is(equalTo("Dubois")));
            }

            @Test
            void noFamilyMembers_impliesNoPartner() {
                var person = person("00600099536");
                assert person.familyMembers().isEmpty();

                Assertions.assertTrue(person.partner().isEmpty());
            }
        }
        
    }
    
    @Nested
    class UnrelatedResidents {
        private RelatedPerson member1;
        private RelatedPerson member2;
        private RelatedPerson member3;
        
        @BeforeEach
        void setup() {
            var members = person("67722499896").unrelatedResidents();
            assertThat(members, hasSize(3));

            member1 = members.get(0);
            member2 = members.get(1);
            member3 = members.get(2);
        }
        
        @Test
        void mapsInsz() {
            assertAll(
                    () -> assertThat(member1.insz(), is(equalTo("71690999827"))),
                    () -> assertThat(member2.insz(), is(equalTo("00631499791"))),
                    () -> assertThat(member3.insz(), is(equalTo("02621599895"))));
        }

        @Test
        void mapsInszOptional() {
            assertAll(
                    () -> assertThat(member1.inszOptional(), is(equalTo(Optional.of("71690999827")))),
                    () -> assertThat(member2.inszOptional(), is(equalTo(Optional.of("00631499791")))),
                    () -> assertThat(member3.inszOptional(), is(equalTo(Optional.of("02621599895")))));
        }
        
        @Test
        void mapsFirstName() {
            assertAll(
                    () -> assertThat(member1.firstName(), is(equalTo("Isabelle Giovanna Mariette"))),
                    () -> assertThat(member2.firstName(), is(equalTo("Hans"))),
                    () -> assertThat(member3.firstName(), is(equalTo("Patricia"))));
        }
        
        @Test
        void mapsLastName() {
            assertAll(
                    () -> assertThat(member1.lastName(), is(equalTo("Janssens"))),
                    () -> assertThat(member2.lastName(), is(equalTo("Janssens"))),
                    () -> assertThat(member3.lastName(), is(equalTo("Janssens"))));
        }

        @Test
        void noFamilyMembers_impliesNoUnrelatedMembers() {
            var person = person("00600099536");
            assert person.familyMembers().isEmpty();

            Assertions.assertTrue(person.unrelatedResidents().isEmpty());
        }
    }
    
    private Person person(String insz) {
        var response = MagdaMock.getInstance()
                                .getPerson(insz);
        return new MagdaResponsePerson(new MagdaResponseWrapper(response));
    }
}
