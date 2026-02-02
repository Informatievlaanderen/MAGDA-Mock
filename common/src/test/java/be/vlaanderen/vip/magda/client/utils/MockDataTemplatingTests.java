package be.vlaanderen.vip.magda.client.utils;


import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockDataTemplatingTests {

    private final OffsetDateTime dateTime;

    private MockDataTemplatingTests(){
        this.dateTime = OffsetDateTime.of(1582, 10, 11, 0, 0, 0, 0, ZoneOffset.ofHours(0));
    }

    @Test
    public void templatingShouldNotChangeStaticString(){
        String staticString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis nibh.";
        String templatedString = MockDataTemplating.processTemplatingValues(staticString, OffsetDateTime.now());
        assertEquals(staticString, templatedString);
    }

    @Test
    public void testTemplatingForDays(){
        String templatedDateString = MockDataTemplating.processDateTemplate("-10d", dateTime);
        assertEquals("1582-10-01", templatedDateString);
        templatedDateString = MockDataTemplating.processDateTemplate("-102d", dateTime);
        assertEquals("1582-07-01", templatedDateString);
    }

    @Test
    public void testTemplatingForStartQuarter(){
        String templatedDateString = MockDataTemplating.processDateTemplate("sq", dateTime);
        assertEquals("1582-10-01", templatedDateString);
    }

    @Test
    public void testTemplatingForYear(){
        String templatedDateString = MockDataTemplating.processDateTemplate("-5y", dateTime);
        assertEquals("1577-10-11", templatedDateString);
    }

    @Test
    public void testTemplatingForMonths(){
        String templatedDateString = MockDataTemplating.processDateTemplate("+17m", dateTime);
        assertEquals("1584-03-11", templatedDateString);
    }

    @Test
    public void testTemplatingForCombination(){
        String templatedDateString = MockDataTemplating.processDateTemplate("-1y,sq,+1m,-124d,sq", dateTime);
        assertEquals("1581-04-01", templatedDateString);
    }
}
