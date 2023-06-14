package be.vlaanderen.vip.mock.magda.inventory;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.CaseFile;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.ServiceDirectory;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder.VersionDirectory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class TestcaseFinder {
    private final ResourceFinder finder;
    
    public TestcaseFinder(ResourceFinder finder) {
        this.finder = finder;
    }

    /**
     * A Testcase 'unique' identifier is the INSZ number
     * Additional information is what services can be called for that INSZ number
     * 
     * E.g:
     * Persoon
     *   GeefBewijs
     *     02.00.00
     *       12345.xml
     *       67890.xml
     *   GeefAttest
     *     02.00.00
     *       12345.xml
     *       54321.xml
     *       
     * Would result in 3 Testcases, and specifically for '12345' the Testcase
     * would have 2 services: GeefBewijs/02.00.00 and GeeftAttest/02.00.00
     */
    public List<Testcase> listServices(String type) {
        var servicesAndCases = getServicesWithCases(type);
        return getAllTestCaseNames(servicesAndCases).stream()
                                                    .map(caseName -> toTestcase(type, caseName, servicesAndCases))
                                                    .toList();
    }
    
    private Testcase toTestcase(String type, String caseName, Map<TestcaseService, Set<String>> servicesAndCases) {
        return new Testcase(caseName, 
                            getCaseDescription(type, caseName), 
                            TestcaseType.DOMAIN, // TODO was initially hardcoded to DOMAIN, I assume this will need to be updated
                            getServicesForCase(caseName, servicesAndCases));
    }
    
    private String getCaseDescription(String type, String caseName) {
        try(var is = finder.loadSimulatorResource(type, caseName + ".txt")) {
            if(is != null) {
                return new String(is.readAllBytes());
            }
        } catch (IOException e) {
            log.debug("Failed to read description for %s - %s".formatted(type, caseName), e);
        }
        return "Testcase " + caseName;
    }
    
    /**
     * Key: services
     * Value: cases for that services
     * 
     * E.g:
     * Persoon
     *   GeefBewijs
     *     02.00.00
     *       12345.xml
     *       67890.xml
     *     
     * with parameter 'Persoon' will result in the entry:
     * ((service: "GeefBewijs", version: "02.00.00"), <"12345", "67890">)
     */
    private Map<TestcaseService, Set<String>> getServicesWithCases(String type) {
        record ServiceVersion(ServiceDirectory sd, VersionDirectory vd) {}
        
        return finder.listServicesDirectories(type)
                     .stream()
                     .flatMap(sd -> sd.versions()
                                      .stream()
                                      .map(vd -> new ServiceVersion(sd, vd)))
                     .collect(Collectors.toMap(sv -> toTestcaseService(sv.sd(), sv.vd()), 
                                               sv -> getCases(sv.vd())));
    }
    
    /**
     * Returns all INSZ's there are cases for
     * 
     * E.g:
     * Persoon
     *   GeefBewijs
     *     02.00.00
     *       12345.xml
     *       67890.xml
     *   GeefAttest
     *     02.00.00
     *       12345.xml
     *       54321.xml
     *       
     * Would result into ["12345", "54321", "67890"]
     */
    private Set<String> getAllTestCaseNames(Map<TestcaseService, Set<String>> servicesAndCases) {
        return servicesAndCases.values()
                               .stream()
                               .flatMap(Collection::stream)
                               .collect(Collectors.toSet());
    }
    
    private List<TestcaseService> getServicesForCase(String caseName, Map<TestcaseService, Set<String>> serviceCases) {
        return serviceCases.entrySet()
                           .stream()
                           .filter(e -> e.getValue().contains(caseName))
                           .map(Entry::getKey)
                           .toList();
    }
    
    private TestcaseService toTestcaseService(ServiceDirectory sd, VersionDirectory vd) {
        return new TestcaseService(sd.service(), vd.version());
    }
    
    private Set<String> getCases(VersionDirectory vd) {
        return vd.cases()
                 .stream()
                 .map(CaseFile::name)
                 .collect(Collectors.toSet());
    }
}
