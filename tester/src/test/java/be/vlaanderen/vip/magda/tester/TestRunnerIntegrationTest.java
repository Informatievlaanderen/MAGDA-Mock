package be.vlaanderen.vip.magda.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunnerIntegrationTest {

    public static class PassingTest {
        @Test
        void passes() {}
    }

    public static class FailingTest {
        @Test
        void fails() { fail(); }
    }

    @Test
    void runAllPassingTests() {
        var classSelectors = List.of(selectClass(PassingTest.class));
        var testRunner = new TestRunner(classSelectors);

        assertEquals(TestRunner.Result.PASS, testRunner.run());
    }

    @Test
    void runSomeFailingTests() {
        var classSelectors = List.of(selectClass(PassingTest.class), selectClass(FailingTest.class));
        var testRunner = new TestRunner(classSelectors);

        assertEquals(TestRunner.Result.FAIL, testRunner.run());
    }
}
