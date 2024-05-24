package be.vlaanderen.vip.magda.client.domain.repertoriumregister;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.dto.INSZ;
import be.vlaanderen.vip.magda.client.xml.node.Node;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagdaClientRepertoriumRegistrationServiceTest {
    @Mock private MagdaClient magdaClient;
    
    @InjectMocks
    private MagdaClientRepertoriumRegistrationService service;
    
    @Nested
    class Register {

        @Test
        void callsMagdaService() throws MagdaClientException {
            mockResultResponse("1");
            var insz = INSZ.of("00000000097");
            var start = LocalDate.now();

            var requestRegister = RegistreerInschrijvingRequest.builder()
                    .insz(insz.getValue())
                    .startDate(start)
                    .build();

            requestRegister.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            var registeredInsz = service.register(requestRegister);

            assertNotNull(registeredInsz);
            assertEquals(insz, registeredInsz.insz());

            verify(magdaClient).send(argThat(r -> {
                if(r instanceof RegistreerInschrijvingRequest request) {
                    return request.getInsz().getValue().equals("00000000097") &&
                           request.getStartDate().equals(start) &&
                           request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }
        
        @Test
        void throwsException_whenResultNot1() {
            mockResultResponse("0");
            var insz = INSZ.of("00000000097");
            var start = LocalDate.now();
            
            assertThrows(MagdaClientException.class,
                         () -> service.register(RegistreerInschrijvingRequest.builder()
                                 .insz(insz.getValue())
                                 .startDate(start)
                                 .build()));
        }
        
        private void mockResultResponse(String value) {
            var node = mock(Node.class);
            when(node.getValue()).thenReturn(Optional.of(value));
            var response = mock(MagdaResponseWrapper.class);
            when(response.getNode("//Antwoord/Inhoud/Resultaat")).thenReturn(Optional.of(node));
            try {
                when(magdaClient.send(any())).thenReturn(response);
            } catch (MagdaClientException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
}
