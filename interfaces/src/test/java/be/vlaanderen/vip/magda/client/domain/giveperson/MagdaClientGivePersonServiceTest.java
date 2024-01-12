package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaClientGivePersonServiceTest {
    @Mock private MagdaClient magdaClient;
    @Mock private CorrelationHeaderProvider correlationHeaderProvider;

    @InjectMocks
    private MagdaClientGivePersonService service;

    @Nested
    class GetPerson {

        @BeforeEach
        void setup() {
            when(correlationHeaderProvider.getXCorrelationId()).thenReturn(Optional.of("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
        }

        @Test
        void callsMagdaService() throws MagdaClientException {
            service.getPerson(GeefPersoonRequest.builder()
                    .insz("insz")
                    .build());
            
            verify(magdaClient).send(argThat(r -> {
                if(r instanceof GeefPersoonRequest request) {
                    return request.getInsz().getValue().equals("insz") &&
                           request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }
        
    }
}
