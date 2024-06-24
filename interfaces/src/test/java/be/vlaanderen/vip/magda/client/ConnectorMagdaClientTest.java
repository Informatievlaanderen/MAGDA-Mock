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

import java.util.ArrayList;
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
        
        private final List<UitzonderingEntry> level2errors = new ArrayList<>();
        private final List<UitzonderingEntry> level3errors = new ArrayList<>();
        
        @BeforeEach
        void setup() {
            lenient().when(response.getUitzonderingEntries()).thenReturn(level2errors);
            lenient().when(response.getResponseUitzonderingEntries()).thenReturn(level3errors);
            lenient().when(response.getRequestId()).thenReturn(REQUEST_ID);
        }
        
        @Test
        void returnsMagdaResponse() throws MagdaClientException {
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
            when(connector.send(request, REQUEST_ID)).thenReturn(response);
            level2errors.add(mock(UitzonderingEntry.class));

            assertThrows(MagdaClientException.class,
                         () -> service.send(request, REQUEST_ID));
        }
        
        @Test
        void throwsException_whenResponseContainsLevel3ErrorsAtLeastOneFout() {
            when(connector.send(request, REQUEST_ID)).thenReturn(response);
            UitzonderingEntry uitzonderingEntry = mock(UitzonderingEntry.class);
            when(uitzonderingEntry.getUitzonderingType()).thenReturn(UitzonderingType.FOUT);
            level3errors.add(uitzonderingEntry);

            assertThrows(MagdaClientException.class,
                         () -> service.send(request, REQUEST_ID));
        }

        @Test
        void throwsException_whenResponseContainsLevel3ErrorsNoFout() {
            when(connector.send(request, REQUEST_ID)).thenReturn(response);
            UitzonderingEntry uitzonderingEntry = mock(UitzonderingEntry.class);
            when(uitzonderingEntry.getUitzonderingType()).thenReturn(UitzonderingType.WAARSCHUWING);
            level3errors.add(uitzonderingEntry);

            assertDoesNotThrow(
                    () -> service.send(request, REQUEST_ID));
        }
    }
}
