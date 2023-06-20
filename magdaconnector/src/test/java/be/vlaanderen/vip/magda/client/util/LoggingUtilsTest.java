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
import static be.vlaanderen.vip.magda.client.util.LoggingUtils.magdaAanvraagLoggingEventBuilder;
import static org.mockito.Mockito.*;

class LoggingUtilsTest {

    private static final String GEEF_BEWIJS = "GeefBewijs";
    private static final String VERSIE = "02.00.0000";

    @Test
    void testMakeMagdaLoggingEventBuilder() {
        var mockLogger = mock(Logger.class);
        var mockAanvraag = Mockito.mock(MagdaRequest.class);
        var mockEventBuilder = mock(LoggingEventBuilder.class, RETURNS_SELF);

        var service = new MagdaServiceIdentification(GEEF_BEWIJS, VERSIE);

        when(mockLogger.makeLoggingEventBuilder(any())).thenReturn(mockEventBuilder);
        when(mockAanvraag.magdaServiceIdentification()).thenReturn(service);

        magdaAanvraagLoggingEventBuilder(mockLogger, Level.INFO, mockAanvraag);

        verify(mockLogger).makeLoggingEventBuilder(Level.INFO);
        verify(mockEventBuilder).addKeyValue(MAGDA_SERVICE_NAME.getKey(), GEEF_BEWIJS);
        verify(mockEventBuilder).addKeyValue(MAGDA_SERVICE_VERSION.getKey(), VERSIE);
    }
}
