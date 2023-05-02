package be.vlaanderen.vip.mock.magda.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import be.vlaanderen.vip.mock.magda.TempDirExtension;
import be.vlaanderen.vip.mock.magda.TempDirExtension.TempDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.CaseFile;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.VersionDirectory;

@ExtendWith(TempDirExtension.class)
class ResourceFinderTest {

    @Nested
    class ServiceDirectoryTests {
        
        @Nested
        class Service {
            
            @Test
            void isFileName(TempDirectory dir) {
                var serviceDir = new ServiceDirectory(dir.createDir("folder-name"));
                
                assertThat(serviceDir.service(), is(equalTo("folder-name")));
            }
            
        }
        
        @Nested
        class Versions {
            
            @Test
            void isAListOfFoldersInDir(TempDirectory dir) {
                var serviceDir = new ServiceDirectory(dir.createDir("folder"));
                var v1 = dir.createDir("folder/version1");
                var v2 = dir.createDir("folder/version2");
                dir.createFile("folder/file1", "file1-content");
                var v3 = dir.createDir("folder/version3");
                
                assertThat(serviceDir.versions(), contains(versionDir(v1), 
                                                           versionDir(v2), 
                                                           versionDir(v3)));
            }
            
            private Matcher<VersionDirectory> versionDir(File dir) {
                return new BaseMatcher<>() {

                    @Override
                    public boolean matches(Object actual) {
                        if(actual instanceof VersionDirectory vd) {
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
            void isFileName(TempDirectory dir) {
                var versionDir = new VersionDirectory(dir.createDir("folder-name"));
                
                assertThat(versionDir.version(), is(equalTo("folder-name")));
            }
            
        }
        
        @Nested
        class Cases {
            
            @Test
            void isAListOfFilesInDir(TempDirectory dir) {
                var versionDir = new VersionDirectory(dir.createDir("folder"));
                var c1 = dir.createFile("folder/case1.xml", "");
                var c2 = dir.createFile("folder/case2.xml", "");
                dir.createDir("folder/folder");
                var c3 = dir.createFile("folder/case3.xml", "");
                
                assertThat(versionDir.cases(), contains(caseDir(c1), 
                                                        caseDir(c2), 
                                                        caseDir(c3)));
            }
            
            @Test
            void filtersOutFilesWithUnsupportedExtensions(TempDirectory dir) {
                var versionDir = new VersionDirectory(dir.createDir("folder"));
                var c1 = dir.createFile("folder/case1.xml", "");
                var c2 = dir.createFile("folder/case2.json", "");
                var c3 = dir.createFile("folder/case3.pdf", "");
                var c4 = dir.createFile("folder/case4.jpg", "");
                dir.createFile("folder/case5.txt", "");
                dir.createFile("folder/case6.other", "");
                
                assertThat(versionDir.cases(), contains(caseDir(c1), 
                                                        caseDir(c2), 
                                                        caseDir(c3), 
                                                        caseDir(c4)));
            }
            
            private Matcher<CaseFile> caseDir(File file) {
                return new BaseMatcher<>() {

                    @Override
                    public boolean matches(Object actual) {
                        if(actual instanceof CaseFile cf) {
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
            void isFileName(TempDirectory dir) {
                var caseFile = new CaseFile(dir.createDir("case-name"));
                
                assertThat(caseFile.name(), is(equalTo("case-name")));
            }
            
        }
    }
    
}
