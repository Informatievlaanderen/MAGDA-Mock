package be.vlaanderen.vip.mock.magda.inventory;

import static be.vlaanderen.vip.mock.magda.TempDirExtension.TempDirectory.FileDescriptor.file;
import static java.util.Map.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import be.vlaanderen.vip.mock.magda.TempDirExtension;
import be.vlaanderen.vip.mock.magda.TempDirExtension.TempDirectory;

@ExtendWith(TempDirExtension.class)
class TestcaseFinderTest {
    private ResourceFinder finder;

    private TestcaseFinder caseFinder;
    
    @Nested
    class ListServices {
        
        @BeforeEach
        void setup(TempDirectory dir) {
            dir.createFileStructure(fileStructure());

            finder = ResourceFinders.directory(dir.getDir());
            caseFinder = new TestcaseFinder(finder);
        }
        
        private Map<Object, Object> fileStructure() {
            return of("Persoon", of("GeefAttest",      geefAttest(),
                                    "GeefBewijs",      geefBewijs(),
                                    "GeefPersoon",     getPersoon(),
                                    "00071031644.txt", file("description for 00071031644")));
        }
        
        private Map<Object, Object> geefAttest() {
            return of("02.00.0000", of("14021699891.xml", file(),
                                       "99051615128.xml", file()));
        }
        
        private Map<Object, Object> geefBewijs() {
            return of("02.00.0000", of("00071031644.xml", file(),
                                       "79052300232.xml", file()));
        }
        
        private Map<Object, Object> getPersoon() {
            return of("02.02.0000", of("00071031644.xml", file(),
                                       "91010100144.xml", file()));
        }
        
        @Test
        void createsTestcase_forEachUniqueCase() {
            var result = caseFinder.listServices("Persoon");
            
            assertAll(
                    () -> assertThat(result, hasSize(5)),
                    () -> assertThat(result, hasItems(withCase(withKey("14021699891")),
                                                      withCase(withKey("99051615128")),
                                                      withCase(withKey("00071031644")),
                                                      withCase(withKey("79052300232")),
                                                      withCase(withKey("91010100144")))));
        }
        
        @Test
        void empty_whenNoCasesForType() {
            var result = caseFinder.listServices("Unknown");
            
            assertThat(result, is(empty()));
        }
        
        @Test
        void getDescriptionFromTxtFile_orFallbackWhenNoTextFile() {
            var result = caseFinder.listServices("Persoon");
            
            assertThat(result, hasItems(withCase(withDescription("Testcase 14021699891")),
                                        withCase(withDescription("Testcase 99051615128")),
                                        withCase(withDescription("description for 00071031644")),
                                        withCase(withDescription("Testcase 79052300232")),
                                        withCase(withDescription("Testcase 91010100144"))));
        }
        
        @Test
        void hasTypeDomain() {
            var result = caseFinder.listServices("Persoon");
            
            assertThat(result, hasItems(withCase(withType(TestcaseType.DOMAIN)),
                                        withCase(withType(TestcaseType.DOMAIN)),
                                        withCase(withType(TestcaseType.DOMAIN)),
                                        withCase(withType(TestcaseType.DOMAIN)),
                                        withCase(withType(TestcaseType.DOMAIN))));
        }
        
        @Test
        void containsRelatedServices() {
            var result = caseFinder.listServices("Persoon");
            
            assertThat(result, hasItems(withCase(withKey("14021699891").and(withServices(attest()))),
                                        withCase(withKey("99051615128").and(withServices(attest()))),
                                        withCase(withKey("00071031644").and(withServices(bewijs(), persoon()))),
                                        withCase(withKey("79052300232").and(withServices(bewijs()))),
                                        withCase(withKey("91010100144").and(withServices(persoon())))));
        }
        
        private Predicate<Testcase> withKey(String key) {
            return c -> c.getKey().equals(key);
        }
        
        private Predicate<Testcase> withDescription(String description) {
            return c -> c.getDescription().equals(description);
        }
        
        private Predicate<Testcase> withType(TestcaseType type) {
            return c -> c.getType().equals(type);
        }
        
        private Predicate<Testcase> withServices(TestcaseService... services) {
            return c -> c.getServices().containsAll(Arrays.asList(services)) &&
                        c.getServices().size() == services.length;
        }
        
        private TestcaseService attest() {
            return new TestcaseService("GeefAttest", "02.00.0000");
        }
        
        private TestcaseService bewijs() {
            return new TestcaseService("GeefBewijs", "02.00.0000");
        }
        
        private TestcaseService persoon() {
            return new TestcaseService("GeefPersoon", "02.02.0000");
        }
        
        private Matcher<Testcase> withCase(Predicate<Testcase> test) {
            return new BaseMatcher<Testcase>() {

                @Override
                public boolean matches(Object actual) {
                    if(actual instanceof Testcase testcase) {
                        return test.test(testcase);
                    }
                    return false;
                }

                @Override
                public void describeTo(Description description) { }
            };
        }
        
    }
}
