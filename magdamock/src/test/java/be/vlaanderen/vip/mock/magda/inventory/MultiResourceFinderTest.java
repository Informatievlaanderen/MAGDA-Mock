package be.vlaanderen.vip.mock.magda.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;

@ExtendWith(MockitoExtension.class)
class MultiResourceFinderTest {
    @Mock(strictness = Strictness.LENIENT)
    private ResourceFinder finder1;
    @Mock(strictness = Strictness.LENIENT)
    private ResourceFinder finder2;
    @Mock(strictness = Strictness.LENIENT)
    private ResourceFinder finder3;

    private MultiResourceFinder finder;
    
    @BeforeEach
    void setup() {
        finder = MultiResourceFinder.create(finder1, finder2, finder3);
    }
    
    @Nested
    class LoadSimulatorResource {
        @Mock private InputStream is1;
        @Mock private InputStream is2;
        @Mock private InputStream is3;
        
        @Test
        void returnsFirstMatchingInputStream() {
            when(finder1.loadSimulatorResource("type", "resource")).thenReturn(null);
            when(finder2.loadSimulatorResource("type", "resource")).thenReturn(is2);
            when(finder3.loadSimulatorResource("type", "resource")).thenReturn(is3);
            
            var result = finder.loadSimulatorResource("type", "resource");
            
            assertThat(result, is(equalTo(is2)));
        }
        
        @Test
        void null_whenNoFinderHasResource() {
            when(finder1.loadSimulatorResource("type", "resource")).thenReturn(null);
            when(finder2.loadSimulatorResource("type", "resource")).thenReturn(null);
            when(finder3.loadSimulatorResource("type", "resource")).thenReturn(null);
            
            var result = finder.loadSimulatorResource("type", "resource");
            
            assertThat(result, is(nullValue()));
        }
        
    }
    
    @Nested
    class ListServicesDirectories {
        
        @Test
        void joinsServiceDirectoriesOfAllFinders() {
            var dir1 = serivceDir();
            var dir2 = serivceDir();
            var dir3 = serivceDir();
            var dir4 = serivceDir();

            when(finder1.listServicesDirectories("type")).thenReturn(Arrays.asList(dir1));
            when(finder2.listServicesDirectories("type")).thenReturn(Arrays.asList(dir2, dir3));
            when(finder3.listServicesDirectories("type")).thenReturn(Arrays.asList(dir4));
            
            var result = finder.listServicesDirectories("type");
            
            assertThat(result, contains(dir1, dir2, dir3, dir4));
        }
        
        private ServiceDirectory serivceDir() {
            return mock(ServiceDirectory.class);
        }
    }
}
