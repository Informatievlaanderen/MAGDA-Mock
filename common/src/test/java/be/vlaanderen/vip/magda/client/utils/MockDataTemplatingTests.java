package be.vlaanderen.vip.magda.client.utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockDataTemplatingTests {

    @Test
    public void templatingShouldNotChangeStaticString(){
        String staticString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis nibh.";
        String templatedString = MockDataTemplating.processTemplatingValues(staticString);
        assertEquals(staticString, templatedString);
    }
}
