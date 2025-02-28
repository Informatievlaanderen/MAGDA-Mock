package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.PersonSource.KSZ;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class GeefPersoonRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = GeefPersoonRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456")
                    .source(KSZ);
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertEquals(INSZNumber.of("123"), request.getInsz());
            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals("456", keyRegistration.getKey());
            assertEquals(KSZ, request.getSource());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = GeefPersoonRequest.builder()
                    .insz(INSZNumber.of("123"));
            var request = builder.build();

            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = GeefPersoonRequest.builder()
                    .insz("123");
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = GeefPersoonRequest.builder();

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void source_isOptional() {
            var builder = GeefPersoonRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456");
            var request = builder.build();

            assertNull(request.getSource());
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private GeefPersoonRequest.Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = GeefPersoonRequest.builder()
                    .insz(INSZNumber.of(TestBase.TEST_INSZ))
                    .registration("456");
        }

        @Test
        void setsFields() {
            var requestDocument = builder
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(requestDocument.getValue("//Inhoud/Criteria/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(requestDocument.getValue("//Inhoud/Bron"), is(equalTo("RR")));
            assertNull(requestDocument.getValue("//Inhoud/Criteria/OpvragingenKSZ"));
            assertNull(requestDocument.getValue("//Inhoud/Criteria/OpvragingenKSZ/BasisPersoonsgegevens"));
        }

        @Test
        void setsFields_withCustomSource() {
            var requestDocument = builder
                    .source(KSZ)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(requestDocument.getValue("//Inhoud/Criteria/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(requestDocument.getValue("//Inhoud/Bron"), is(equalTo("KSZ")));
            assertThat(requestDocument.getValue("//Inhoud/Criteria/OpvragingenKSZ/BasisPersoonsgegevens"), is(equalTo("1")));
        }
    }
}