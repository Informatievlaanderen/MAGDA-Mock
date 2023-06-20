package be.vlaanderen.vip.magda.client.util;

import be.vlaanderen.vip.magda.client.Aanvraag;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

public class LoggingUtils {
    private LoggingUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static LoggingEventBuilder magdaAanvraagLoggingEventBuilder(Logger logger, Level level, Aanvraag aanvraag) {
        return logger.makeLoggingEventBuilder(level)
                .addKeyValue(LoggingKeys.MAGDA_SERVICE_NAME.getKey(), aanvraag.magdaServiceIdentification().getNaam())
                .addKeyValue(LoggingKeys.MAGDA_SERVICE_VERSION.getKey(), aanvraag.magdaServiceIdentification().getVersie());
    }
}
