package be.vlaanderen.vip.magda.client;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestHelpers {
    public static String getResourceAsString(Class<?> clazz, String resourceName) throws IOException {
        return IOUtils.toString(Objects.requireNonNull(clazz.getResourceAsStream(resourceName)), StandardCharsets.UTF_8);
    }
}
