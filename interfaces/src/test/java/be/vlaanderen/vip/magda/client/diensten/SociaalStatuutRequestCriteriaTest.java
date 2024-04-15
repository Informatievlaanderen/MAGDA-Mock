package be.vlaanderen.vip.magda.client.diensten;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SociaalStatuutRequestCriteriaTest {

    @Nested
    class SociaalStatuutRequestCriteriaBuilder {

        @Test
        void buildsCriteria_whenDateNotNull() {
            var date = LocalDate.now();

            var request = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(date)
                    .locationName("REGIO_GENT")
                    .build();

            assertEquals("sociaal-statuut", request.getSocialStatusName());
            assertEquals(date, request.getDate());
            assertEquals("REGIO_GENT", request.getLocationName());
        }

        @Test
        void buildsCriteria_whenPeriodNotNull() {
            var startDate = LocalDate.now();
            var endDate = LocalDate.now().plusYears(1);

            var request = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut")
                    .startDate(startDate)
                    .endDate(endDate)
                    .locationName("REGIO_GENT")
                    .build();

            assertEquals("sociaal-statuut", request.getSocialStatusName());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
            assertEquals("REGIO_GENT", request.getLocationName());
        }

        @Test
        void throwsException_whenSocialStatusNameNull() {
            var builder = SociaalStatuutRequestCriteria.builder()
                    .date(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateNullAndStartDateNull() {
            var builder = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut");

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateNotNullAndStartDateNotNull() {
            var builder = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now())
                    .startDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenStartDateNullAndEndDateNotNull() {
            var builder = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now())
                    .endDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void buildsCriteria_whenLocationNameNull() {
            var date = LocalDate.now();

            var request = SociaalStatuutRequestCriteria.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(date)
                    .build();

            assertNull(request.getLocationName());
        }
    }
}
