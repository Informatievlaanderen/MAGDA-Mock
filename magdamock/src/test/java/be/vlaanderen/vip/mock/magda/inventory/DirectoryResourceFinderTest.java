package be.vlaanderen.vip.mock.magda.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
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
import org.junit.jupiter.api.io.TempDir;

import be.vlaanderen.vip.mock.magda.TempDirUtils;
import be.vlaanderen.vip.mock.magda.inventory.DirectoryResourceFinder.FileCaseFile;
import be.vlaanderen.vip.mock.magda.inventory.DirectoryResourceFinder.FileServiceDirectory;
import be.vlaanderen.vip.mock.magda.inventory.DirectoryResourceFinder.FileVersionDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.CaseFile;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.VersionDirectory;

class DirectoryResourceFinderTest {
    private DirectoryResourceFinder finder;
    
    @TempDir
    private File dir;
    
    @BeforeEach
    void setup() {
        finder = new DirectoryResourceFinder(dir);
    }
    
    @Nested
    class LoadSimulatorResource {
        
        @Test
        void returnsFileAsInputStream() throws IOException {
            TempDirUtils.createFile(dir, "type/path/to/resource.txt", "content");
            
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
        void returnsServiceDirectory_forType() {
            var dir1 = TempDirUtils.createDir(dir, "type/dir1");
            var dir2 = TempDirUtils.createDir(dir, "type/dir2");
            TempDirUtils.createDir(dir, "other-type/dir3");
            TempDirUtils.createFile(dir, "file1.txt", "file1");
            
            var result = finder.listServicesDirectories("type");
            
            assertThat(result, contains(serviceDirectoryFor(dir1),
                                        serviceDirectoryFor(dir2)));
        }
        
        @Test
        void isEmptyWhenNoDirForType() {
            TempDirUtils.createDir(dir, "type/dir1");
            TempDirUtils.createDir(dir, "type/dir2");

            var result = finder.listServicesDirectories("other-type");
            
            assertThat(result, is(empty()));
        }
        
        private Matcher<ServiceDirectory> serviceDirectoryFor(File dir) {
            return new BaseMatcher<ServiceDirectory>() {

                @Override
                public boolean matches(Object actual) {
                    if(actual instanceof FileServiceDirectory sd) {
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

    @Nested
    class ServiceDirectoryTests {
        
        @Nested
        class Service {
            
            @Test
            void isFileName() {
                var serviceDir = new FileServiceDirectory(TempDirUtils.createDir(dir, "folder-name"));
                
                assertThat(serviceDir.service(), is(equalTo("folder-name")));
            }
            
        }
        
        @Nested
        class Versions {
            
            @Test
            void isAListOfFoldersInDir() {
                var serviceDir = new FileServiceDirectory(TempDirUtils.createDir(dir, "folder"));
                var v1 = TempDirUtils.createDir(dir, "folder/version1");
                var v2 = TempDirUtils.createDir(dir, "folder/version2");
                TempDirUtils.createFile(dir, "folder/file1", "file1-content");
                var v3 = TempDirUtils.createDir(dir, "folder/version3");
                
                assertThat(serviceDir.versions(), contains(versionDir(v1), 
                                                           versionDir(v2), 
                                                           versionDir(v3)));
            }
            
            private Matcher<VersionDirectory> versionDir(File dir) {
                return new BaseMatcher<>() {

                    @Override
                    public boolean matches(Object actual) {
                        if(actual instanceof FileVersionDirectory vd) {
                            return vd.file().equals(dir);
                        }
                        return false;
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("VersionDirectory(%s)".formatted(dir));
                    }
                    
                };
            }
        }
        
    }
    
    @Nested
    class VersionDirectoryTests {
        
        @Nested
        class Version {
            
            @Test
            void isFileName() {
                var versionDir = new FileVersionDirectory(TempDirUtils.createDir(dir, "folder-name"));
                
                assertThat(versionDir.version(), is(equalTo("folder-name")));
            }
            
        }
        
        @Nested
        class Cases {
            
            @Test
            void isAListOfFilesInDir() {
                var versionDir = new FileVersionDirectory(TempDirUtils.createDir(dir, "folder"));
                var c1 = TempDirUtils.createFile(dir, "folder/case1.xml", "");
                var c2 = TempDirUtils.createFile(dir, "folder/case2.xml", "");
                TempDirUtils.createDir(dir, "folder/folder");
                var c3 = TempDirUtils.createFile(dir, "folder/case3.xml", "");
                
                assertThat(versionDir.cases(), contains(caseDir(c1), 
                                                        caseDir(c2), 
                                                        caseDir(c3)));
            }
            
            @Test
            void filtersOutFilesWithUnsupportedExtensions() {
                var versionDir = new FileVersionDirectory(TempDirUtils.createDir(dir, "folder"));
                var c1 = TempDirUtils.createFile(dir, "folder/case1.xml", "");
                var c2 = TempDirUtils.createFile(dir, "folder/case2.json", "");
                var c3 = TempDirUtils.createFile(dir, "folder/case3.pdf", "");
                var c4 = TempDirUtils.createFile(dir, "folder/case4.jpg", "");
                TempDirUtils.createFile(dir, "folder/case5.txt", "");
                TempDirUtils.createFile(dir, "folder/case6.other", "");
                
                assertThat(versionDir.cases(), contains(caseDir(c1), 
                                                        caseDir(c2), 
                                                        caseDir(c3), 
                                                        caseDir(c4)));
            }
            
            private Matcher<CaseFile> caseDir(File file) {
                return new BaseMatcher<>() {

                    @Override
                    public boolean matches(Object actual) {
                        if(actual instanceof FileCaseFile cf) {
                            return cf.file().equals(file);
                        }
                        return false;
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("CaseFile(%s)".formatted(file));
                    }
                    
                };
            }
        }
        
    }
    
    @Nested
    class CaseFileTests {

        
        @Nested
        class Name {
            
            @Test
            void isFileName() {
                var caseFile = new FileCaseFile(TempDirUtils.createDir(dir, "case-name"));
                
                assertThat(caseFile.name(), is(equalTo("case-name")));
            }
            
        }
    }
    
}
