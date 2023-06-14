package be.vlaanderen.vip.magda.tester;

import be.vlaanderen.vip.bluegreen.TestRunner;
import be.vlaanderen.vip.magda.tester.tests.MockServerHttpTest;
import be.vlaanderen.vip.magda.tester.tests.TestcasesTest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@SpringBootApplication
public class TesterApplication {

    private static final List<ClassSelector> classSelectors = List.of(
            selectClass(TestcasesTest.class),
            selectClass(MockServerHttpTest.class)
    );

    public static void main(String[] args) {
        var result = new TestRunner(classSelectors).run();

        System.exit(result == TestRunner.Result.PASS ? 0 : 1);
    }
}