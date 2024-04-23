package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.SociaalStatuutRequestCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MagdaClientSocialStatutesServiceTest {

    @Mock
    private MagdaClient magdaClient;
    @Mock private CorrelationHeaderProvider correlationHeaderProvider;

    @InjectMocks
    private MagdaClientSocialStatutesService service;

    @BeforeEach
    void setup() {
        when(correlationHeaderProvider.getXCorrelationId()).thenReturn(Optional.of("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
    }

    @Test
    void callsMagdaService() throws MagdaClientException {

        var date = LocalDate.now();
        Set<SociaalStatuutRequestCriteria> requestCriteria = Set.of(SociaalStatuutRequestCriteria.builder().socialStatusName("INCREASED_COMPENSATION").startDate(date).endDate(date).build());

        GeefMultipleSociaalStatuutRequest multipleSociaalStatuutRequest = GeefMultipleSociaalStatuutRequest.builder()
                .insz("insz")
                .registration("registration")
                .socialStatutes(requestCriteria).build();

        service.getSocialStatutes(multipleSociaalStatuutRequest);

        verify(magdaClient).send(argThat(magdaRequest -> {
            if (magdaRequest instanceof GeefMultipleSociaalStatuutRequest request) {
                return request.getInsz().getValue().equals("insz") &&
                        request.getRegistration().equals("registration") &&
                        request.getSocialStatutes().equals(requestCriteria) &&
                        request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            }
            return false;
        }));
    }
}
