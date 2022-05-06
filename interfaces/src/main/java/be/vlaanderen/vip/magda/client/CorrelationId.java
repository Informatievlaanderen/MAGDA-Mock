package be.vlaanderen.vip.magda.client;

import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationId {
    private static final String CORRELATION_ID = "labels.correlationId";
    private static final ThreadLocal<UUID> correlationId = new ThreadLocal<>();

    public static synchronized UUID get() {
        UUID value = correlationId.get();
        if (value == null) {
            value = UUID.randomUUID();
            set(value);
        }
        return value;
    }

    public static void set(UUID value) {
        correlationId.set(value);
        MDC.put(CORRELATION_ID, value.toString());
    }

    public static void clear() {
        correlationId.remove();
        MDC.remove(CORRELATION_ID);
    }

    public static boolean isPresent() {
        return correlationId.get() != null;
    }

    @Override
    public String toString() {
        return correlationId.get().toString();
    }
}
