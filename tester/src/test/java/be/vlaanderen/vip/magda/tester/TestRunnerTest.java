package be.vlaanderen.vip.magda.tester;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestRunnerTest {

    @Test
    void allTestsPass() {
        try (var launcherDiscoveryRequestBuilderStatic = Mockito.mockStatic(LauncherDiscoveryRequestBuilder.class);
             var launcherFactoryStatic = Mockito.mockStatic(LauncherFactory.class)) {
            var classSelectors = List.of(mock(ClassSelector.class));

            var launcherDiscoveryRequestBuilder = mock(LauncherDiscoveryRequestBuilder.class);
            launcherDiscoveryRequestBuilderStatic.when(LauncherDiscoveryRequestBuilder::request).thenReturn(launcherDiscoveryRequestBuilder);
            when(launcherDiscoveryRequestBuilder.selectors(classSelectors)).thenReturn(launcherDiscoveryRequestBuilder);
            var launcherDiscoverRequest = mock(LauncherDiscoveryRequest.class);
            when(launcherDiscoveryRequestBuilder.build()).thenReturn(launcherDiscoverRequest);

            var launcher = mock(Launcher.class);
            launcherFactoryStatic.when(LauncherFactory::create).thenReturn(launcher);

            var summary = mock(TestExecutionSummary.class);
            when(summary.getFailures()).thenReturn(List.of());
            when(summary.getTestsSucceededCount()).thenReturn(10L);
            when(summary.getTestsFoundCount()).thenReturn(10L);

            var listener = Mockito.mock(SummaryGeneratingListener.class);
            when(listener.getSummary()).thenReturn(summary);

            var testRunner = new TestRunner(classSelectors, listener);

            assertEquals(TestRunner.Result.PASS, testRunner.run());

            verify(launcher, times(1)).registerTestExecutionListeners(listener);
            verify(launcher, times(1)).execute(launcherDiscoverRequest);
        }
    }

    @Test
    void someTestsFail() {
        try (var launcherDiscoveryRequestBuilderStatic = Mockito.mockStatic(LauncherDiscoveryRequestBuilder.class);
             var launcherFactoryStatic = Mockito.mockStatic(LauncherFactory.class)) {
            var classSelectors = List.of(mock(ClassSelector.class));

            var launcherDiscoveryRequestBuilder = mock(LauncherDiscoveryRequestBuilder.class);
            launcherDiscoveryRequestBuilderStatic.when(LauncherDiscoveryRequestBuilder::request).thenReturn(launcherDiscoveryRequestBuilder);
            when(launcherDiscoveryRequestBuilder.selectors(classSelectors)).thenReturn(launcherDiscoveryRequestBuilder);
            var launcherDiscoverRequest = mock(LauncherDiscoveryRequest.class);
            when(launcherDiscoveryRequestBuilder.build()).thenReturn(launcherDiscoverRequest);

            var launcher = mock(Launcher.class);
            launcherFactoryStatic.when(LauncherFactory::create).thenReturn(launcher);

            var summary = mock(TestExecutionSummary.class);
            when(summary.getFailures()).thenReturn(List.of());
            when(summary.getTestsSucceededCount()).thenReturn(5L);
            when(summary.getTestsFoundCount()).thenReturn(10L);

            var listener = Mockito.mock(SummaryGeneratingListener.class);
            when(listener.getSummary()).thenReturn(summary);

            var testRunner = new TestRunner(classSelectors, listener);

            assertEquals(TestRunner.Result.FAIL, testRunner.run());

            verify(launcher, times(1)).registerTestExecutionListeners(listener);
            verify(launcher, times(1)).execute(launcherDiscoverRequest);
        }
    }
}
