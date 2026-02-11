package be.vlaanderen.vip.magda.client.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockDataTemplatingTests {
    private final LocalDate dateTime;

    private MockDataTemplatingTests(){
        this.dateTime = LocalDate.of(1582, 10, 11);
    }

    @Test
    public void templatingShouldNotChangeStaticString(){
        String staticString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis nibh.";
        String templatedString = MockDataTemplating.processTemplatingValues(staticString, LocalDate.now());
        assertEquals(staticString, templatedString);
    }

    @Test
    public void templateingShouldFormatDate(){
        String staticString = "Today is {{formatDate now}}";
        String templatedString = MockDataTemplating.processTemplatingValues(staticString, dateTime);
        assertEquals("Today is 1582-10-11", templatedString);
    }

    @Test
    public void testTemplatingForDays(){
        String templatedDateString = MockDataTemplating.processTemplatingValues("{{formatDate (dateMath now '-10d')}}", dateTime);
        assertEquals("1582-10-01", templatedDateString);
        templatedDateString = MockDataTemplating.processTemplatingValues("{{formatDate (dateMath now '-102d')}}", dateTime);
        assertEquals("1582-07-01", templatedDateString);
    }

    @Test
    public void testTemplatingForStartOfQuarter(){
        String templatedDateString = MockDataTemplating.processTemplatingValues("{{formatDate (startOfQuarter now)}}", dateTime);
        assertEquals("1582-10-01", templatedDateString);
    }

    @Test
    public void testTemplatingForEndOfQuarter(){
        String templatedDateString = MockDataTemplating.processTemplatingValues("{{formatDate (endOfQuarter now)}}", dateTime);
        assertEquals("1582-12-31", templatedDateString);
    }


    @Test
    public void testTemplatingForCombination(){
        String templatedDateString = MockDataTemplating.processTemplatingValues("{{formatDate (endOfQuarter (dateMath now '-102d'))}}", dateTime);
        assertEquals("1582-09-30", templatedDateString);
    }
}
