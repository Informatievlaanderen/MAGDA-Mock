package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.SociaalStatuutRequestCriteria;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MagdaClientSocialStatuteServiceTest {
    @Mock private MagdaClient magdaClient;
    @Mock
    private MagdaResponseSocialStatutesAdapter magdaResponseSocialStatutesAdapter;

    @InjectMocks
    private MagdaClientSocialStatuteService service;


    @Nested
    class GetSocialStatute {
        
        @Test
        void callsMagdaService() throws MagdaClientException {
            var date = LocalDate.now();

            var requestSociaalStatuut = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("social-statute")
                    .date(date)
                    .build();

            requestSociaalStatuut.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            service.getSocialStatutes(requestSociaalStatuut);
            
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

            var requestSociaalStatuut = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("social-statute")
                    .date(date)
                    .locationName("REGIO_GENT")
                    .build();

            requestSociaalStatuut.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            service.getSocialStatutes(requestSociaalStatuut);

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

    @Test
    void callsMagdaServiceMultipleSociaalStatuutRequest() throws MagdaClientException {

        var date = LocalDate.now();
        Set<SociaalStatuutRequestCriteria> requestCriteria = Set.of(SociaalStatuutRequestCriteria.builder().socialStatusName("INCREASED_COMPENSATION").startDate(date).endDate(date).build());

        GeefMultipleSociaalStatuutRequest multipleSociaalStatuutRequest = GeefMultipleSociaalStatuutRequest.builder()
                .insz("insz")
                .registration("registration")
                .socialStatutes(requestCriteria).build();
        multipleSociaalStatuutRequest.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

        service.getSocialStatutesByMultipleCriteria(multipleSociaalStatuutRequest);

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
