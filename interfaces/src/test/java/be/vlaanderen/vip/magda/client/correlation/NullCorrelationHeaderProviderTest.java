package be.vlaanderen.vip.magda.client.correlation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NullCorrelationHeaderProviderTest {

    @Test
    void getCorrelationId_alwaysReturnsEmpty() {
        var xCorrelationId = NullCorrelationHeaderProvider.getInstance().getXCorrelationId();
        assertNotNull(xCorrelationId);
        assertFalse(xCorrelationId.isPresent());
    }
}