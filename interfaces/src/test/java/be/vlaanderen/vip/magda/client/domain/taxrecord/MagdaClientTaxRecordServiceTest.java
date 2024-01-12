package be.vlaanderen.vip.magda.client.domain.taxrecord;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaClientTaxRecordServiceTest {

    @Mock
    private MagdaClient magdaClient;

    @Mock
    private CorrelationHeaderProvider correlationHeaderProvider;

    @InjectMocks
    private MagdaClientTaxRecordService service;

    @Nested
    class GetTaxRecordListForIncomeYear {

        @BeforeEach
        void setup() {
            when(correlationHeaderProvider.getXCorrelationId()).thenReturn(Optional.of("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
        }

        @Test
        void callsMagdaService() throws MagdaClientException {
            service.getTaxRecordList(GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("insz")
                    .incomeYear(Year.of(2021))
                    .build());

            verify(magdaClient).send(argThat(r -> {
                if(r instanceof GeefAanslagbiljetPersonenbelastingRequest request) {
                    return request.getInsz().getValue().equals("insz") && request.getIncomeYear().equals(Year.of(2021)) &&
                           request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }
    }
}