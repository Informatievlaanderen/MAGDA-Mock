package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domain.dto.Kwartaal;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeefDmfaVoorWerknemerRequestTest {
    @Nested
    class GeefDmfaVoorWerknemerRequestBuilder {
        @Test
        void buildsRequest() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var request = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(beginKwartaal, request.getBeginKwartaal());
            assertEquals(eindeKwartaal, request.getEindKwartaal());
            assertEquals(GeefDmfaVoorWerknemerRequest.Bron.RSZ, request.getBron());
            assertEquals(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE, request.getTypeAntwoord());
            assertEquals(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES, request.getLaatsteSituatie());
        }

        @Test
        void throwsExceptionWhenInszNull() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var builder = GeefDmfaVoorWerknemerRequest.builder()
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsExceptionWhenBeginKwartaalNull() {
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var builder = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .eindeKwartaal(eindeKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsExceptionWhenEindeKwartaalNull() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            var builder = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void bronIsOptional() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var request = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build();

            assertNull(request.getBron());
        }

        @Test
        void typeAntwoordIsOptional() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var request = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build();

            assertNull(request.getTypeAntwoord());
        }

        @Test
        void laatsteSituatieIsOptional() {
            Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
            Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();
            var request = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal)
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .build();

            assertNull(request.getLaatsteSituatie());
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private GeefDmfaVoorWerknemerRequest.Builder builder;
        private final Kwartaal beginKwartaal = new Kwartaal(2023, 1).verify();
        private final Kwartaal eindeKwartaal = new Kwartaal(2025, 2).verify();

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("hoedanigheid")
                    .build();
            builder = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal);
        }

        @Test
        void setsAllFields(){
            var request = builder
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.DIBISS)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertThat(request.getValue("//Criteria/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//Criteria/Kwartaal/Begin/Jaar"), is(equalTo(String.valueOf(beginKwartaal.jaar()))));
            assertThat(request.getValue("//Criteria/Kwartaal/Begin/Kwartaalcijfer"), is(equalTo(String.valueOf(beginKwartaal.kwartaalcijfer()))));
            assertThat(request.getValue("//Criteria/Kwartaal/Einde/Jaar"), is(equalTo(String.valueOf(eindeKwartaal.jaar()))));
            assertThat(request.getValue("//Criteria/Kwartaal/Einde/Kwartaalcijfer"), is(equalTo(String.valueOf(eindeKwartaal.kwartaalcijfer()))));
            assertThat(request.getValue("//Bron"), is(equalTo(GeefDmfaVoorWerknemerRequest.Bron.DIBISS.getTypeString())));
            assertThat(request.getValue("//TypeAntwoord"), is(equalTo(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE.getTypeString())));
            assertThat(request.getValue("//LaatsteSituatie"), is(equalTo(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES.getTypeString())));
        }

        @Test
        void bronEmpty(){
            builder = GeefDmfaVoorWerknemerRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .beginKwartaal(beginKwartaal)
                    .eindeKwartaal(eindeKwartaal);
            var request = builder
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertThat(request.xpath("//Bron").getLength(), is(0));
        }
        @Test
        void typeAntwoordEmpty(){
            var request = builder
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .laatsteSituatie(GeefDmfaVoorWerknemerRequest.LaatsteSituatie.ALLE_SITUATIES)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertThat(request.xpath("//TypeAntwoord").getLength(), is(0));
        }
        @Test
        void laatsteSituatieEmpty(){
            var request = builder
                    .bron(GeefDmfaVoorWerknemerRequest.Bron.RSZ)
                    .typeAntwoord(GeefDmfaVoorWerknemerRequest.TypeAntwoord.ONLINE)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertThat(request.xpath("//LaatsteSituatie").getLength(), is(0));
        }
    }
}
