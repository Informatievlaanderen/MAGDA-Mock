package be.vlaanderen.vip.mock.magda.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import be.vlaanderen.vip.mock.magda.TempDirExtension;
import be.vlaanderen.vip.mock.magda.TempDirExtension.TempDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;

@ExtendWith(TempDirExtension.class)
class DirectoryResourceFinderTest {
    private DirectoryResourceFinder finder;
    
    @BeforeEach
    void setup(TempDirectory dir) {
        finder = new DirectoryResourceFinder(dir.getDir());
    }
    
    @Nested
    class LoadSimulatorResource {
        
        @Test
        void returnsFileAsInputStream(TempDirectory dir) throws IOException {
            dir.createFile("type/path/to/resource.txt", "content");
            
            try(var result = finder.loadSimulatorResource("type", "path/to/resource.txt")) {
                assertEquals("content", new String(result.readAllBytes()));
            }
        }
        
        @Test
        void isNull_whenFileDoesNotExist() {
            var result = finder.loadSimulatorResource("type", "path/to/resource.txt");
            
            assertNull(result);
        }
        
    }
    
    @Nested
    class ListServicesDirectories {
        
        @Test
        void returnsServiceDirectory_forType(TempDirectory dir) {
            var dir1 = dir.createDir("type/dir1");
            var dir2 = dir.createDir("type/dir2");
            dir.createDir("other-type/dir3");
            dir.createFile("file1.txt", "file1");
            
            var result = finder.listServicesDirectories("type");
            
            assertThat(result, contains(serviceDirectoryFor(dir1),
                                        serviceDirectoryFor(dir2)));
        }
        
        @Test
        void isEmptyWhenNoDirForType(TempDirectory dir) {
            dir.createDir("type/dir1");
            dir.createDir("type/dir2");

            var result = finder.listServicesDirectories("other-type");
            
            assertThat(result, is(empty()));
        }
        
        private Matcher<ServiceDirectory> serviceDirectoryFor(File dir) {
            return new BaseMatcher<ServiceDirectory>() {

                @Override
                public boolean matches(Object actual) {
                    if(actual instanceof ServiceDirectory sd) {
                        return sd.dir().equals(dir);
                    }
                    return false;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("no service directory for %s".formatted(dir));
                }
            };
        }
        
    }
    
}
