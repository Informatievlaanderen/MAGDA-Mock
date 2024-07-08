package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefHistoriekInschrijvingRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource.HO;
import static be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource.INT;
import static org.mockito.Mockito.verify;

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

        @Test
        void callsMagdaService() throws MagdaClientException {

            var request = GeefHistoriekInschrijvingRequest.builder()
                    .insz("insz")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2025, 1, 1))
                    .sources(Set.of(HO, INT))
                    .build();

            request.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            service.getEnrollmentHistory(request);

            verify(magdaClient).send(request);
        }
    }
}