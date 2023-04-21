package be.vlaanderen.vip.magda.client.util;

import lombok.Getter;

@Getter
public enum LoggingKeys {
    MAGDA_SERVICE_NAME("magdaServiceName"),
    MAGDA_SERVICE_VERSION("magdaServiceVersion");

    private final String key;

    LoggingKeys(String key) {
        this.key = key;
    }
}
