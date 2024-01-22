package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefWerkrelatiesRequestTest {

    @Nested
    class GeefWerkrelatiesRequestBuilder {

        @Test
        void buildsRequest() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var request = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .source(GeefWerkrelatiesRequest.SourceType.RSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.STARTED_OR_ENDED_ONLY)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.NON_DELETED)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.NON_INTERIM_ONLY)
                    .build();
            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(GeefWerkrelatiesRequest.SourceType.RSZ, request.getSource());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
            assertEquals(GeefWerkrelatiesRequest.StartedOrEndedType.STARTED_OR_ENDED_ONLY, request.getStartedOrEndedType());
            assertEquals(GeefWerkrelatiesRequest.DeletionIndicationType.NON_DELETED, request.getDeletionIndicationType());
            assertEquals(GeefWerkrelatiesRequest.InterimIndicationType.NON_INTERIM_ONLY, request.getInterimIndicationType());
        }

        @Test
        void throwsException_whenInszNull() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var builder = GeefWerkrelatiesRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenStartDateNull() {
            var endDate = LocalDate.of(2023,12,31);
            var builder = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDeletionIndicationTypeNull() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var builder = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void sourceIsOptional() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var request = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.STARTED_OR_ENDED_ONLY)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.NON_DELETED)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.NON_INTERIM_ONLY)
                    .build();
            assertNull(request.getSource());
        }

        @Test
        void endDateIsOptional() {
            var startDate = LocalDate.of(2023, 1,1);
            var request = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL)
                    .build();
            assertNull(request.getEndDate());
        }

        @Test
        void startedOrEndedTypeIsOptional() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var request = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.ALL)
                    .build();
            assertNull(request.getStartedOrEndedType());
        }

        @Test
        void interimIndicationTypeIsOptional() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var request = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.ALL)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .build();
            assertNull(request.getInterimIndicationType());
        }
    }

    @Nested
    class ToMagdaDocument {
        private MagdaRegistrationInfo info;
        private GeefWerkrelatiesRequest.Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();
            builder = GeefWerkrelatiesRequest.builder()
                    .insz(TestBase.TEST_INSZ);
        }

        @Test
        void setsFields() {

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .source(GeefWerkrelatiesRequest.SourceType.RSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .endDate(LocalDate.of(2023,12,31))
                    .startedOrEndedType(GeefWerkrelatiesRequest.StartedOrEndedType.STARTED_OR_ENDED_ONLY)
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.NON_DELETED)
                    .interimIndicationType(GeefWerkrelatiesRequest.InterimIndicationType.NON_INTERIM_ONLY)
                    .build()
                    .toMagdaDocument(info);
            assertThat(request.getValue("//Criteria/Relatie/Werknemer/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//Criteria/Bron"), is(equalTo(GeefWerkrelatiesRequest.SourceType.RSZ.getTypeString())));
            assertThat(request.getValue("//Criteria/Periode/Begin"), is(equalTo("2023-01-01")));
            assertThat(request.getValue("//Criteria/Periode/Einde"), is(equalTo("2023-12-31")));
            assertThat(request.getValue("//Criteria/EnkelGestartOfBeeindigd"), is(equalTo(GeefWerkrelatiesRequest.StartedOrEndedType.STARTED_OR_ENDED_ONLY.getTypeString())));
            assertThat(request.getValue("//Criteria/SchrappingsIndicatie"), is(equalTo(GeefWerkrelatiesRequest.DeletionIndicationType.NON_DELETED.getTypeString())));
            assertThat(request.getValue("//Criteria/InterimIndicatie"), is(equalTo(GeefWerkrelatiesRequest.InterimIndicationType.NON_INTERIM_ONLY.getTypeString())));
        }

        @Test
        void doesNotSetSourceIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.xpath("//Criteria/Bron").getLength(), is(0));
        }

        @Test
        void doesNotSetEndDateIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/Periode/Einde"), is(nullValue()));
        }

        @Test
        void doesNotSetStartedOrEndedTypeIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/EnkelGestartOfBeeindigd"), is(nullValue()));
        }

        @Test
        void doesNotSetInterimIndicationTypeIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .deletionIndicationType(GeefWerkrelatiesRequest.DeletionIndicationType.ALL)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/InterimIndicatie"), is(nullValue()));
        }
    }
}