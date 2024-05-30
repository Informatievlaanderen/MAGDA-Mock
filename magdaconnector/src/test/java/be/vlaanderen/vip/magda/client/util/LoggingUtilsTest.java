package be.vlaanderen.vip.magda.client.util;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

import static be.vlaanderen.vip.magda.client.util.LoggingKeys.MAGDA_SERVICE_NAME;
import static be.vlaanderen.vip.magda.client.util.LoggingKeys.MAGDA_SERVICE_VERSION;
import static be.vlaanderen.vip.magda.client.util.LoggingUtils.magdaRequestLoggingEventBuilder;
import static org.mockito.Mockito.*;

class LoggingUtilsTest {

    private static final String GEEF_BEWIJS = "GeefBewijs";
    private static final String VERSIE = "02.00.0000";

    @Test
    void testMakeMagdaLoggingEventBuilder() {
        var mockLogger = mock(Logger.class);
        var mockRequest = Mockito.mock(MagdaRequest.class);
        var mockEventBuilder = mock(LoggingEventBuilder.class, RETURNS_SELF);

        var serviceId = new MagdaServiceIdentification(GEEF_BEWIJS, VERSIE);

        when(mockLogger.atLevel(any())).thenReturn(mockEventBuilder);
        when(mockRequest.magdaServiceIdentification()).thenReturn(serviceId);

        magdaRequestLoggingEventBuilder(mockLogger, Level.INFO, mockRequest);

        verify(mockLogger).atLevel(Level.INFO);
        verify(mockEventBuilder).addKeyValue(MAGDA_SERVICE_NAME.getKey(), GEEF_BEWIJS);
        verify(mockEventBuilder).addKeyValue(MAGDA_SERVICE_VERSION.getKey(), VERSIE);
    }
}
