package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefFunctiesByPersonRequestTest {

    @Nested
    class GeefFunctiesByPersonRequestBuilder {

        @Test
        void buildsRequest() {
            var kboNumber = KBONumber.of(TestBase.TEST_KBO_NUMBER);
            var functionTypes = Set.of("10006", "10010");
            var request = GeefFunctiesByPersonRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .kboNumber(kboNumber)
                    .functionTypes(functionTypes)
                    .build();
            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(kboNumber, request.getKboNumber());
            assertEquals(functionTypes, request.getFunctionTypes());
        }

        @Test
        void throwsException_whenInszNull() {
            var kboNumber = KBONumber.of(TestBase.TEST_KBO_NUMBER);
            var functionTypes = Set.of("10006", "10010");
            var builder = GeefFunctiesByPersonRequest.builder()
                    .kboNumber(kboNumber)
                    .functionTypes(functionTypes);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void kboNumberIsOptional() {
            var functionTypes = Set.of("10006", "10010");
            var request = GeefFunctiesByPersonRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .functionTypes(functionTypes)
                    .build();
            assertNull(request.getKboNumber());
        }

        @Test
        void functionTypesIsOptional() {
            var kboNumber = KBONumber.of(TestBase.TEST_KBO_NUMBER);
            var request = GeefFunctiesByPersonRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .kboNumber(kboNumber)
                    .build();
            assertNull(request.getFunctionTypes());
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private GeefFunctiesByPersonRequest.Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();
            builder = GeefFunctiesByPersonRequest.builder()
                    .insz(TestBase.TEST_INSZ);
        }

        @Test
        void setsFields() {
            var kboNumber = KBONumber.of(TestBase.TEST_KBO_NUMBER);
            var functionTypes = Set.of("10006", "10010");

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .kboNumber(kboNumber)
                    .functionTypes(functionTypes)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertThat(request.getValue("//Criteria/Persoon/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//Criteria/Persoon/Ondernemingsnummer"), is(equalTo(kboNumber.getValue())));
            assertThat(request.getValues("//Criteria/AardFuncties/AardFunctie"), is(functionTypes.stream().toList()));
        }

        @Test
        void doesNotSetKboNumberIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/Persoon/Ondernemingsnummer"), is(nullValue()));
        }

        @Test
        void doesNotSetFunctionTypesIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.xpath("//Criteria/AardFuncties").getLength(), is(0));
        }
    }
}