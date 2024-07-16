package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource.HO;
import static be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource.INT;
import static be.vlaanderen.vip.magda.legallogging.model.UitzonderingType.FOUT;
import static be.vlaanderen.vip.magda.legallogging.model.UitzonderingType.WAARSCHUWING;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaClientGiveEnrollmentHistoryServiceTest {

    @Mock
    private MagdaClient magdaClient;
    @Mock
    private MagdaResponseEnrollmentHistoryAdapter adapter;

    @InjectMocks
    private MagdaClientGiveEnrollmentHistoryService service;

    @Nested
    class GetEnrollmentHistory {

        private GeefHistoriekInschrijvingRequest request;

        @BeforeEach
        void setup() {
            request = GeefHistoriekInschrijvingRequest.builder()
                    .insz("insz")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2025, 1, 1))
                    .sources(Set.of(HO, INT))
                    .build();

            request.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
        }

        @Test
        void callsMagdaService() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of())
                    .build()));

            service.getEnrollmentHistory(request);

            verify(magdaClient).send(request);
        }

        @Test
        void throwsNoException_ifContainsAnyNonErrorLevel3Uitzondering() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("30101")
                            .uitzonderingType(WAARSCHUWING)
                            .build()))
                    .build()));

            assertDoesNotThrow(() -> service.getEnrollmentHistory(request));
        }

        @Test
        void throwsNoException_ifContainsLevel3Error30101() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("30101")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertDoesNotThrow(() -> service.getEnrollmentHistory(request));
        }

        @Test
        void throwsException_ifContainsOtherLevel3Error() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("12345")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertThrows(MagdaClientException.class, () -> service.getEnrollmentHistory(request));
        }
    }
}