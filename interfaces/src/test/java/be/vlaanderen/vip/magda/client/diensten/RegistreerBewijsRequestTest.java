package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistreerBewijsRequestTest {

    @Nested
    class Builder {

        private RegistreerBewijsRequest.Builder builder;

        @BeforeEach
        void setup() {
            builder = RegistreerBewijsRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .leverancierNaam("leverancierNaam")
                    .leverancierBewijsreferte("leverancierBewijsreferte")
                    .bewijsBasis(TestBewijsBasis.COMPLETE);
        }

        @Test
        void buildsRequest() {
            var request = builder.build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals("leverancierNaam", request.getLeverancierNaam());
            assertEquals("leverancierBewijsreferte", request.getLeverancierBewijsreferte());
            assertEquals(TestBewijsBasis.COMPLETE, request.getBewijsBasis());
        }

        @Test
        void throwsException_whenInszNull() {
            builder.insz((INSZNumber) null);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenLeverancierNaamNull() {
            builder.leverancierNaam(null);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenLeverancierBewijsReferteNull() {
            builder.leverancierBewijsreferte(null);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenBewijsBasisNull() {
            builder.bewijsBasis(null);

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("341f14f3-b0ad-4da3-afa3-c5c6b05dabdf");

        private MagdaRegistrationInfo info;
        private RegistreerBewijsRequest.Builder builder;

        @BeforeEach
        void setup() {

            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = RegistreerBewijsRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .leverancierNaam("leverancierNaam")
                    .leverancierBewijsreferte("leverancierBewijsreferte");
        }

        @Test
        void setsFieldsForCompleteBewijsBasis() {
            var request = builder
                    .bewijsBasis(TestBewijsBasis.COMPLETE)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertAll(
                    () -> assertThat(request.getValue("//Bewijsregistratie/Leverancier/Naam"), is(equalTo("leverancierNaam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Leverancier/Bewijsreferte"), is(equalTo("leverancierBewijsreferte"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/PersoonUitgereikt/INSZ"), is(equalTo(TestBase.TEST_INSZ))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Authenticiteit/Naam"), is(equalTo("authenticiteit naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Categorie/Naam"), is(equalTo("categorie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Graad/Naam"), is(equalTo("graad naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwijsvorm/Naam"), is(equalTo("onderwijsvorm naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Bewijstype/Naam"), is(equalTo("bewijstype naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Bewijsstaat/Naam"), is(equalTo("bewijsstaat naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instantie/Naam"), is(equalTo("instantie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwerp/Code"), is(equalTo("onderwerp code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwerp/Naam"), is(equalTo("onderwerp naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Jaar"), is(equalTo("2025"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Maand"), is(equalTo("07"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Dag"), is(equalTo("05"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/VolledigeNaam"), is(equalTo("volledigeNaam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Land/Code"), is(equalTo("land code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Taal/Code"), is(equalTo("taal code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instelling/Naam"), is(equalTo("instelling naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instelling/Nummer"), is(equalTo("instelling nummer"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Schooltype/Naam"), is(equalTo("schooltype naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Studierichting/Naam"), is(equalTo("studierichting naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Studierichting/Code"), is(equalTo("studierichting code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Specialisatie/Naam"), is(equalTo("specialisatie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Specialisatie/Code"), is(equalTo("specialisatie code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/DetailOnderwerp/Naam"), is(equalTo("detailOnderwerp naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/DetailOnderwerp/Code"), is(equalTo("detailOnderwerp code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Vervalperiode"), is(equalTo("vervalperiode"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/UrenVolwassenenonderwijs"), is(equalTo("123"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/AlternatieveInstanties/AlternatieveInstantie[1]/Instantierol/Naam"), is(equalTo("alternatieveInstantie 1 instantierol naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/AlternatieveInstanties/AlternatieveInstantie[1]/Instantie/Naam"), is(equalTo("alternatieveInstantie 1 instantie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/AlternatieveInstanties/AlternatieveInstantie[2]/Instantierol/Naam"), is(equalTo("alternatieveInstantie 2 instantierol naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/AlternatieveInstanties/AlternatieveInstantie[2]/Instantie/Naam"), is(equalTo("alternatieveInstantie 2 instantie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/BijkomendeInformaties/BijkomendeInformatie[1]/Naam"), is(equalTo("bijkomendeInformatie 1 naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/BijkomendeInformaties/BijkomendeInformatie[1]/Inhoud"), is(equalTo("bijkomendeInformatie 1 inhoud"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/BijkomendeInformaties/BijkomendeInformatie[2]/Naam"), is(equalTo("bijkomendeInformatie 2 naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/BijkomendeInformaties/BijkomendeInformatie[2]/Inhoud"), is(equalTo("bijkomendeInformatie 2 inhoud")))
            );
        }

        @Test
        void setsFieldsForBewijsBasisWithNullValues() {
            var request = builder
                    .bewijsBasis(TestBewijsBasis.WITH_NULL_VALUES)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertAll(
                    () -> assertThat(request.getValue("//Bewijsregistratie/Leverancier/Naam"), is(equalTo("leverancierNaam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Leverancier/Bewijsreferte"), is(equalTo("leverancierBewijsreferte"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/PersoonUitgereikt/INSZ"), is(equalTo(TestBase.TEST_INSZ))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Authenticiteit/Naam"), is(equalTo("authenticiteit naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Categorie/Naam"), is(equalTo("categorie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Graad/Naam"), is(equalTo("graad naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwijsvorm/Naam"), is(equalTo("onderwijsvorm naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Bewijstype/Naam"), is(equalTo("bewijstype naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Bewijsstaat/Naam"), is(equalTo("bewijsstaat naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instantie/Naam"), is(equalTo("instantie naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwerp/Code"), is(equalTo("onderwerp code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Onderwerp/Naam"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Jaar"), is(equalTo("2025"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Maand"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Dag"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/VolledigeNaam"), is(equalTo("volledigeNaam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Land/Code"), is(equalTo("land code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Taal/Code"), is(equalTo("taal code"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instelling/Naam"), is(equalTo("instelling naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Instelling/Nummer"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Schooltype/Naam"), is(equalTo("schooltype naam"))),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Studierichting"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Specialisatie"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/DetailOnderwerp"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/Vervalperiode"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/UrenVolwassenenonderwijs"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/AlternatieveInstanties"), is(nullValue())),
                    () -> assertThat(request.getValue("//Bewijsregistratie/Bewijs/BijkomendeInformaties"), is(nullValue()))
            );
        }
    }
}