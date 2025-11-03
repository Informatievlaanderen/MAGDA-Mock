package be.vlaanderen.vip.magda.restclient.util;

import lombok.Getter;

@Getter
enum LoggingKeys {
    MAGDA_SERVICE_NAME("magdaServiceName"),
    MAGDA_SERVICE_VERSION("magdaServiceVersion");

    private final String key;

    LoggingKeys(String key) {
        this.key = key;
    }
}
