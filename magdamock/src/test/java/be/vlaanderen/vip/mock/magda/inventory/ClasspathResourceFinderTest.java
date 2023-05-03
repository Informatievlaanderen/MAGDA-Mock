package be.vlaanderen.vip.mock.magda.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.CaseFile;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.VersionDirectory;

class ClasspathResourceFinderTest {
    private ClasspathResourceFinder finder;
    
    @BeforeEach
    void setup() {
        finder = ClasspathResourceFinder.create("simulator_test", getClass());
    }
    
    @Nested
    class LoadSimulatorResource {
        
        @Test
        void returnsFileAsInputStream() throws IOException {
            try(var result = finder.loadSimulatorResource("Persoon", "GeefAttest/02.00.0000/01010100126.xml")) {
                assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("simulator_test/Persoon/GeefAttest/02.00.0000/01010100126.xml"))));
            }
        }
        
        @Test
        void isNull_whenFileDoesNotExist() {
            var result = finder.loadSimulatorResource("not", "existing-resource");
            
            assertThat(result, is(nullValue()));
        }
        
        private String getResourceContent(String resource) {
            try {
                return new String(getClass().getClassLoader().getResourceAsStream(resource).readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read " + resource);
            }
        }
        
    }
    
    @Nested
    class ListServicesDirectories {
        
        @Test
        void returnsServiceDirectory_forType() {
            var result = finder.listServicesDirectories("Persoon");
            
            assertThat(result, contains(serviceDirectoryFor("GeefAanslagbiljetPersonenbelasting"),
                                        serviceDirectoryFor("GeefAttest"),
                                        serviceDirectoryFor("GeefBewijs")));
        }
        
        @Test
        void isEmptyWhenNoDirForType() {
            var result = finder.listServicesDirectories("Other");
            
            assertThat(result, is(empty()));
        }
        
        private Matcher<ServiceDirectory> serviceDirectoryFor(String service) {
            return new BaseMatcher<ServiceDirectory>() {

                @Override
                public boolean matches(Object actual) {
                    if(actual instanceof ServiceDirectory sd) {
                        return sd.service().equals(service);
                    }
                    return false;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("no service for %s".formatted(service));
                }
            };
        }
        
    }
    
    @Nested
    class ServiceDirectoryTests {
        private ServiceDirectory sd;
        
        @BeforeEach
        void setup() {
            sd = finder.listServicesDirectories("Persoon")
                       .get(0);
        }
        
        @Nested
        class Service {
            
            @Test
            void isFileName() {
                assertThat(sd.service(), is(equalTo("GeefAanslagbiljetPersonenbelasting")));
            }
            
        }
        
        @Nested
        class Versions {
            
            @Test
            void isAListOfFoldersInDir() {
                assertThat(sd.versions(), contains(versionDir("02.00.0000")));
            }
            
            private Matcher<VersionDirectory> versionDir(String version) {
                return new BaseMatcher<>() {

                    @Override
                    public boolean matches(Object actual) {
                        if(actual instanceof VersionDirectory vd) {
                            return vd.version().equals(version);
                        }
                        return false;
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("VersionDirectory(%s)".formatted(version));
                    }
                    
                };
            }
        }
        
        @Nested
        class VersionDirectoryTests {
            private VersionDirectory vd;
            
            @BeforeEach
            void setup() {
                vd = sd.versions()
                       .get(0);
            }
            
            @Nested
            class Version {
                
                @Test
                void isFileName() {
                    assertThat(vd.version(), is(equalTo("02.00.0000")));
                }
                
            }
            
            @Nested
            class Cases {
                
                @Test
                void isAListOfFilesInDir() {
                    assertThat(vd.cases(), contains(caseDir("00010122374.xml"), 
                                                    caseDir("00122099768.xml"), 
                                                    caseDir("51042500288.xml"), 
                                                    caseDir("57082300172.xml")));
                }
                
                private Matcher<CaseFile> caseDir(String name) {
                    return new BaseMatcher<>() {

                        @Override
                        public boolean matches(Object actual) {
                            if(actual instanceof CaseFile cf) {
                                return cf.name().equals(name);
                            }
                            return false;
                        }

                        @Override
                        public void describeTo(Description description) {
                            description.appendText("CaseFile(%s)".formatted(name));
                        }
                        
                    };
                }
            }
            
            @Nested
            class CaseFileTests {
                private CaseFile cf;
                
                @BeforeEach
                void setup() {
                    cf = vd.cases()
                           .get(0);
                }

                @Nested
                class Name {
                    
                    @Test
                    void isFileName() {
                        assertThat(cf.name(), is(equalTo("00010122374.xml")));
                    }
                    
                }
            }
            
        }
        
    }
    
}