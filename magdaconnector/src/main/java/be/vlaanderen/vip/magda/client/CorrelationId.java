package be.vlaanderen.vip.magda.client;

import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationId {
    private static final String LABELS = "labels.correlationId";
    private static final ThreadLocal<UUID> CORRELATION_ID = new ThreadLocal<>();
    
    private CorrelationId() {}

    public static synchronized UUID get() {
        var value = CORRELATION_ID.get();
        if (value == null) {
            value = UUID.randomUUID();
            set(value);
        }
        return value;
    }

    private static synchronized void set(UUID value) {
        CORRELATION_ID.set(value);
        MDC.put(LABELS, value.toString());
    }

    public static void clear() {
        CORRELATION_ID.remove();
        MDC.remove(LABELS);
    }

    public static boolean isPresent() {
        return CORRELATION_ID.get() != null;
    }
}
