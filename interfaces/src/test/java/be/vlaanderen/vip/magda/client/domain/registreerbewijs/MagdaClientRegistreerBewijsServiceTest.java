package be.vlaanderen.vip.magda.client.domain.registreerbewijs;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.RegistreerBewijsRequest;
import be.vlaanderen.vip.magda.client.domain.dto.INSZ;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Bewijs;
import be.vlaanderen.vip.magda.client.xml.node.Node;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagdaClientRegistreerBewijsServiceTest {
    @Mock
    private MagdaClient magdaClient;

    @Mock
    private Bewijs.Basis bewijsBasis;

    @InjectMocks
    private MagdaClientRegistreerBewijsService service;

    @Nested
    class Register {

        @Test
        void callsMagdaService() throws MagdaClientException {
            mockResultResponse("OK");
            var insz = INSZ.of("00000000097");

            var requestRegister = RegistreerBewijsRequest.builder()
                    .insz(insz.getValue())
                    .leverancierNaam("leverancierNaam")
                    .leverancierBewijsreferte("leverancierBewijsreferte")
                    .bewijsBasis(bewijsBasis)
                    .build();

            requestRegister.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));

            assertDoesNotThrow(() -> service.registreerBewijs(requestRegister));

            verify(magdaClient).send(argThat(r -> {
                if(r instanceof RegistreerBewijsRequest request) {
                    return request.getInsz().getValue().equals("00000000097") &&
                            request.getLeverancierNaam().equals("leverancierNaam") &&
                            request.getLeverancierBewijsreferte().equals("leverancierBewijsreferte") &&
                            request.getBewijsBasis().equals(bewijsBasis) &&
                            request.getCorrelationId().equals(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
                }
                return false;
            }));
        }

        @Test
        void throwsException_whenResultNot1() {
            mockResultResponse("NOK");
            var insz = INSZ.of("00000000097");

            assertThrows(MagdaClientException.class,
                    () -> service.registreerBewijs(RegistreerBewijsRequest.builder()
                            .insz(insz.getValue())
                            .leverancierNaam("leverancierNaam")
                            .leverancierBewijsreferte("leverancierBewijsreferte")
                            .bewijsBasis(bewijsBasis)
                            .build()));
        }

        private void mockResultResponse(String value) {
            var node = mock(Node.class);
            when(node.getValue()).thenReturn(Optional.of(value));
            var response = mock(MagdaResponseWrapper.class);
            when(response.getNode("//Antwoord/Inhoud/Melding")).thenReturn(Optional.of(node));
            try {
                when(magdaClient.send(any())).thenReturn(response);
            } catch (MagdaClientException e) {
                throw new RuntimeException(e);
            }
        }
    }
}