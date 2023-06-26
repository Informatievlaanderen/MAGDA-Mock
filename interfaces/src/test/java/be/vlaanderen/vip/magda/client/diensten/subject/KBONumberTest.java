package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KBONumberTest {

    @Test
    void of_constructsKBONumberWithGivenValue() {
        var kbo = KBONumber.of("123");

        assertEquals("123", kbo.getValue());
    }

    @Test
    void of_requiresNonNullValue() {
        assertThrows(IllegalArgumentException.class, () -> KBONumber.of(null));
    }

    @Test
    void subjectType_isAlwaysEnterprise() {
        var kbo = KBONumber.of("123");

        assertEquals(SubjectType.ENTERPRISE, kbo.getSubjectType());
    }

    @Test
    void canBeComparedToEachOther() {
        assertEquals(KBONumber.of("123"), KBONumber.of("123"));
        assertNotEquals(KBONumber.of("456"), KBONumber.of("123"));
    }
}