package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.exception.ServerException;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectorMagdaClientTest {
    @Mock private MagdaConnector connector;
    
    @InjectMocks
    private ConnectorMagdaClient service;
    
    @Nested
    class Send {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        @Mock private MagdaRequest request;
        
        @Mock private MagdaResponse response;
        
        @BeforeEach
        void setup() {
            lenient().when(response.getRequestId()).thenReturn(REQUEST_ID);
        }
        
        @Test
        void returnsMagdaResponse() throws MagdaClientException {
            when(response.getUitzonderingEntries()).thenReturn(List.of());
            when(response.getResponseUitzonderingEntries()).thenReturn(List.of());

            when(connector.send(request, REQUEST_ID)).thenReturn(response);
            
            var result = service.send(request, REQUEST_ID);
            
            assertThat(result.getResponse(), is(equalTo(response)));
        }
        
        @Test
        void throwsException_whenSendFails() {
            when(connector.send(request, REQUEST_ID)).thenThrow(ServerException.class);
            
            assertThrows(MagdaClientException.class,
                         () -> service.send(request, REQUEST_ID));
        }
        
        @Test
        void throwsException_whenResponseContainsLevel2Errors() {
            when(response.getUitzonderingEntries()).thenReturn(List.of(mock(UitzonderingEntry.class)));

            when(connector.send(request, REQUEST_ID)).thenReturn(response);

            assertThrows(MagdaClientException.class,
                         () -> service.send(request, REQUEST_ID));
        }

        @Test
        void returnsMagdaResponse_whenLevel3ErrorsAreNeverSevere() throws MagdaClientException {
            when(response.getUitzonderingEntries()).thenReturn(List.of());
            when(response.getResponseUitzonderingEntries()).thenReturn(List.of(mock(UitzonderingEntry.class)));

            when(connector.send(request, REQUEST_ID)).thenReturn(response);

            var result = service.send(request, REQUEST_ID, uitzonderingEntries -> false);

            assertThat(result.getResponse(), is(equalTo(response)));
        }

        @Test
        void throwsException_whenLevel3ErrorsAreAlwaysSevere() {
            when(response.getUitzonderingEntries()).thenReturn(List.of());
            when(response.getResponseUitzonderingEntries()).thenReturn(List.of(mock(UitzonderingEntry.class)));

            when(connector.send(request, REQUEST_ID)).thenReturn(response);

            assertThrows(MagdaClientException.class,
                    () -> service.send(request, REQUEST_ID, uitzonderingEntries -> true));
        }
        
        @Test
        void throwsException_whenResponseContainsLevel3ErrorsAtLeastOneFout_byDefault() {
            UitzonderingEntry uitzonderingEntry = mock(UitzonderingEntry.class);
            when(uitzonderingEntry.getUitzonderingType()).thenReturn(UitzonderingType.FOUT);

            when(response.getUitzonderingEntries()).thenReturn(List.of());
            when(response.getResponseUitzonderingEntries()).thenReturn(List.of(uitzonderingEntry));

            when(connector.send(request, REQUEST_ID)).thenReturn(response);

            assertThrows(MagdaClientException.class,
                         () -> service.send(request, REQUEST_ID));
        }

        @Test
        void throwsException_whenResponseContainsLevel3ErrorsNoFout_byDefault() {
            UitzonderingEntry uitzonderingEntry = mock(UitzonderingEntry.class);
            when(uitzonderingEntry.getUitzonderingType()).thenReturn(UitzonderingType.WAARSCHUWING);

            when(response.getUitzonderingEntries()).thenReturn(List.of());
            when(response.getResponseUitzonderingEntries()).thenReturn(List.of(uitzonderingEntry));

            when(connector.send(request, REQUEST_ID)).thenReturn(response);

            assertDoesNotThrow(
                    () -> service.send(request, REQUEST_ID));
        }
    }
}
