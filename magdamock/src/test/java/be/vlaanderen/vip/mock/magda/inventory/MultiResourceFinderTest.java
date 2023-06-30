package be.vlaanderen.vip.mock.magda.inventory;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.List;

import static be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    class ListServiceDirectories {
        
        @Test
        void joinsServiceDirectoriesOfAllFinders() {
            var dir1 = serviceDir();
            var dir2 = serviceDir();
            var dir3 = serviceDir();
            var dir4 = serviceDir();

            when(finder1.listServicesDirectories("type")).thenReturn(List.of(dir1));
            when(finder2.listServicesDirectories("type")).thenReturn(List.of(dir2, dir3));
            when(finder3.listServicesDirectories("type")).thenReturn(List.of(dir4));
            
            var result = finder.listServicesDirectories("type");
            
            assertThat(result, contains(dir1, dir2, dir3, dir4));
        }
        
        private ServiceDirectory serviceDir() {
            return mock(ServiceDirectory.class);
        }
    }

    @Nested
    class ListCaseFiles {

        @Test
        void joinsCaseFilesOfAllFinders() {
            var file1 = caseFile();
            var file2 = caseFile();
            var file3 = caseFile();
            var file4 = caseFile();

            when(finder1.listCaseFiles("type", "subdir")).thenReturn(List.of(file1));
            when(finder2.listCaseFiles("type", "subdir")).thenReturn(List.of(file2, file3));
            when(finder3.listCaseFiles("type", "subdir")).thenReturn(List.of(file4));

            var result = finder.listCaseFiles("type", "subdir");

            assertThat(result, contains(file1, file2, file3, file4));
        }

        private CaseFile caseFile() {
            return mock(CaseFile.class);
        }
    }
}
