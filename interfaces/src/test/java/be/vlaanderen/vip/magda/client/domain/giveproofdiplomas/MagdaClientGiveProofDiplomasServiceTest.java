package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static be.vlaanderen.vip.magda.legallogging.model.UitzonderingType.FOUT;
import static be.vlaanderen.vip.magda.legallogging.model.UitzonderingType.WAARSCHUWING;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaClientGiveProofDiplomasServiceTest {

    @Mock
    private MagdaClient magdaClient;
    @Mock
    private MagdaResponseProofDiplomasAdapter adapter;

    @InjectMocks
    private MagdaClientGiveProofDiplomasService service;

    @Nested
    class GetProofDiplomas {

        private GeefBewijsRequest request;

        @BeforeEach
        void setup() {
            request = GeefBewijsRequest.builder()
                    .insz("insz")
                    .build();

            request.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
        }

        @Test
        void callsMagdaService() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of())
                    .build()));

            service.getProofDiplomas(request);

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

            assertDoesNotThrow(() -> service.getProofDiplomas(request));
        }

        @Test
        void throwsNoException_ifContainsLevel3Error30101() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("30101")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertDoesNotThrow(() -> service.getProofDiplomas(request));
        }

        @Test
        void throwsException_ifContainsOtherLevel3Error() throws MagdaClientException {
            when(magdaClient.send(request)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("12345")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertThrows(MagdaClientException.class, () -> service.getProofDiplomas(request));
        }
    }

    @Nested
    class GetProofDiplomasWithRequestId {

        private final UUID REQUEST_ID = UUID.fromString("a887b321-c320-42e9-9fb2-c82834016894");

        private GeefBewijsRequest request;

        @BeforeEach
        void setup() {
            request = GeefBewijsRequest.builder()
                    .insz("insz")
                    .build();

            request.setCorrelationId(UUID.fromString("6469cd5e-e8ed-43f7-a91e-48fdfbb76e0f"));
        }

        @Test
        void callsMagdaService() throws MagdaClientException {
            when(magdaClient.send(request, REQUEST_ID)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of())
                    .build()));

            service.getProofDiplomas(request, REQUEST_ID);

            verify(magdaClient).send(request, REQUEST_ID);
        }

        @Test
        void throwsNoException_ifContainsAnyNonErrorLevel3Uitzondering() throws MagdaClientException {
            when(magdaClient.send(request, REQUEST_ID)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("30101")
                            .uitzonderingType(WAARSCHUWING)
                            .build()))
                    .build()));

            assertDoesNotThrow(() -> service.getProofDiplomas(request, REQUEST_ID));
        }

        @Test
        void throwsNoException_ifContainsLevel3Error30101() throws MagdaClientException {
            when(magdaClient.send(request, REQUEST_ID)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("30101")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertDoesNotThrow(() -> service.getProofDiplomas(request, REQUEST_ID));
        }

        @Test
        void throwsException_ifContainsOtherLevel3Error() throws MagdaClientException {
            when(magdaClient.send(request, REQUEST_ID)).thenReturn(new MagdaResponseWrapper(MagdaResponse.builder()
                    .responseUitzonderingEntries(List.of(UitzonderingEntry.builder()
                            .identification("12345")
                            .uitzonderingType(FOUT)
                            .build()))
                    .build()));

            assertThrows(MagdaClientException.class, () -> service.getProofDiplomas(request, REQUEST_ID));
        }
    }
}