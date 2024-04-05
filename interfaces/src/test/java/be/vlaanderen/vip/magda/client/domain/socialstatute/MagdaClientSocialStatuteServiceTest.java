package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaClientSocialStatuteServiceTest {
    @Mock private MagdaClient magdaClient;
    @Mock private CorrelationHeaderProvider correlationHeaderProvider;

    @InjectMocks
    private MagdaClientSocialStatuteService service;

    @BeforeEach
    void setup() {
        when(correlationHeaderProvider.getXCorrelationId()).thenReturn(Optional.of("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
    }

    @Nested
    class GetSocialStatute {
        
        @Test
        void callsMagdaService() throws MagdaClientException {
            var date = LocalDate.now();

            service.getSocialStatute(GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("social-statute")
                    .date(date)
                    .build());
            
            verify(magdaClient).send(argThat(r -> {
                if(r instanceof GeefSociaalStatuutRequest request) {
                    return request.getInsz().getValue().equals("insz") &&
                           request.getSocialStatusName().equals("social-statute") &&
                           request.getDate().equals(date) &&
                           request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }
        
    }

    @Nested
    class GetSocialStatuteWithLocationName {

        @Test
        void callsMagdaService() throws MagdaClientException {
            var date = LocalDate.now();

            service.getSocialStatute(GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("social-statute")
                    .date(date)
                    .locationName("REGIO_GENT")
                    .build());

            verify(magdaClient).send(argThat(r -> {
                if(r instanceof GeefSociaalStatuutRequest request) {
                    return request.getInsz().getValue().equals("insz") &&
                            request.getSocialStatusName().equals("social-statute") &&
                            request.getLocationName().equals("REGIO_GENT") &&
                            request.getDate().equals(date) &&
                            request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }

    }
}
