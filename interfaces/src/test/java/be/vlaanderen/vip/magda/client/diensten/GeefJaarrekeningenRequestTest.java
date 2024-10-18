package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefJaarrekeningenRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class GeefJaarrekeningenRequestTest {

    private final String kboNumber = "0242069537";

    @Nested
    class GeefJaarrekeningenRequestBuilder {

        @Test
        void buildsRequest() {
            var request = GeefJaarrekeningenRequest.builder()
                    .kboNumber(KBONumber.of(kboNumber))
                    .financialYear(Year.of(2025))
                    .build();

            assertEquals(KBONumber.of(kboNumber), request.getKboNumber());
            assertEquals(Year.of(2025), request.getFinancialYear());
        }

        @Test
        void throwsException_whenKboNumberIsNull() {
            var builder = GeefJaarrekeningenRequest.builder()
                    .financialYear(Year.of(2025));

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = GeefJaarrekeningenRequest.builder();
        }

        @Test
        void setsFields() {
            var request = builder
                    .kboNumber(KBONumber.of(kboNumber))
                    .financialYear(Year.of(2025))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/Ondernemingsnummer"), is(equalTo(kboNumber)));
            assertThat(request.getValue("//Criteria/Boekjaar"), is(equalTo("2025")));
        }
    }
}
