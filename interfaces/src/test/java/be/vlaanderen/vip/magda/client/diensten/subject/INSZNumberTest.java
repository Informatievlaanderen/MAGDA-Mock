package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class INSZNumberTest {

    @Test
    void of_constructsINSZNumberWithGivenValue() {
        var insz = INSZNumber.of("123");

        assertEquals("123", insz.getValue());
    }

    @Test
    void of_requiresNonNullValue() {
        assertThrows(IllegalArgumentException.class, () -> INSZNumber.of(null));
    }

    @Test
    void subjectType_isAlwaysPerson() {
        var insz = INSZNumber.of("123");

        assertEquals(SubjectType.PERSON, insz.getSubjectType());
    }

    @Test
    void xpathExpression_isAlwaysINSZ() {
        var insz = INSZNumber.of("123");

        assertEquals("//INSZ", insz.getXPathExpression());
    }

    @Test
    void getValueInLogFormat_givesValueInExpectedFormat() {
        assertEquals("123 (INSZ)", INSZNumber.of("123").getValueInLogFormat());
    }

    @Test
    void canBeComparedToEachOther() {
        assertEquals(INSZNumber.of("123"), INSZNumber.of("123"));
        assertNotEquals(INSZNumber.of("456"), INSZNumber.of("123"));
    }
}