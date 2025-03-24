package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeefAanslagbiljetPersonenbelastingRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz(INSZNumber.of("123"))
                    .registration("456")
                    .incomeYear(Year.of(2021))
                    .ipcalCodes(List.of("7001", "7002", "7003"));
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertEquals(INSZNumber.of("123"), request.getInsz());
            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals("456", keyRegistration.getKey());
            assertEquals(Year.of(2021), request.getIncomeYear());
            assertEquals(List.of("7001", "7002", "7003"), request.getIpcalCodes());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz(INSZNumber.of("123"))
                    .incomeYear(Year.of(2021));
            var request = builder.build();

            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("123")
                    .incomeYear(Year.of(2021));
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .incomeYear(Year.of(2021));

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void incomeYear_mustBeGiven() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("123");

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void ipcalCodes_areNullWhenNotGiven() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("123")
                    .incomeYear(Year.of(2021));
            var request = builder.build();

            assertNull(request.getIpcalCodes());
        }
    }

    @Nested
    class Deprecated {

        @Nested
        class Builder {

            @Test
            void buildsRequest() {
                var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
                        .insz(INSZNumber.of("123"))
                        .registration("456");
                var request = builder.build();

                assertNull(request.getCorrelationId());
                assertEquals(INSZNumber.of("123"), request.getInsz());
                var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
                assertEquals("456", keyRegistration.getKey());
            }

            @Test
            void registration_HasDefaultValue() {
                var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
                        .insz(INSZNumber.of("123"));
                var request = builder.build();

                var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
                assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
            }

            @Test
            void insz_canBeGivenAsString() {
                var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
                        .insz("123");
                var request = builder.build();

                assertEquals(INSZNumber.of("123"), request.getInsz());
            }

            @Test
            void insz_mustBeGiven() {
                var builder = GeefAanslagbiljetPersonenbelastingRequest.builder();

                assertThrows(IllegalStateException.class, builder::build);
            }
        }
    }
}