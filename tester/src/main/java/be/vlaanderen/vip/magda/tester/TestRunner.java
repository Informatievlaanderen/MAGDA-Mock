package be.vlaanderen.vip.magda.tester;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.List;

@Slf4j
public class TestRunner {

    public enum Result {
        PASS,
        FAIL
    }

    private List<ClassSelector> classSelectors;
    private SummaryGeneratingListener listener;

    TestRunner(List<ClassSelector> classSelectors, SummaryGeneratingListener listener) {
        this.classSelectors = classSelectors;
        this.listener = listener;
    }

    public TestRunner(List<ClassSelector> classSelectors) {
        this(classSelectors, new SummaryGeneratingListener());
    }

    public Result run() {
        log.debug("Initializing test runner...");
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(classSelectors)
                .build();
        var launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);

        log.info("Running blue/green deployment tests...");
        launcher.execute(request);

        var summary = listener.getSummary();
        var failures = summary.getFailures();

        for(var failure : failures) {
            log.error("FAILURE in test '" + failure.getTestIdentifier().getDisplayName() + "': " + failure.getException().getMessage());
            log.error(ExceptionUtils.getStackTrace(failure.getException()));
        }

        if(summary.getTestsSucceededCount() == summary.getTestsFoundCount()) {
            log.info("All tests succeeded.");
            return Result.PASS;
        } else {
            log.warn(summary.getTestsSucceededCount() + "/" + summary.getTestsFoundCount() + " tests passed.");
            return Result.FAIL;
        }
    }
}
