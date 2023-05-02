package be.vlaanderen.vip.mock.magda.inventory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCaseInventoryBuilder {
    private static final String PERSOON_TESTCASES = "Persoon";
    private static final String ONDERNEMING_TESTCASES = "Onderneming";
    private final ResourceFinderDeprecated finder;

    public TestCaseInventoryBuilder() {
        finder = new ResourceFinderDeprecated();
    }

    public List<Testcase> persoonTestcases() throws IOException {
        return enumerateTestcasesIn(PERSOON_TESTCASES);
    }

    public List<Testcase> ondernemingTestcases() throws IOException {
        return enumerateTestcasesIn(ONDERNEMING_TESTCASES);
    }

    private ArrayList<Testcase> enumerateTestcasesIn(String type) throws IOException {
        var result = new ArrayList<Testcase>();
        var allTestCases = new HashSet<String>();
        var servicesWithCases = new HashMap<TestcaseService, Set<String>>();
        var defaultedServices = new HashSet<TestcaseService>();

        var services = finder.listServices(type);

        for (var service : services) {
            var files = finder.listCases(type, service);
            var cases = new HashSet<String>();
            for (var file : files) {
                var name = FilenameUtils.getBaseName(file);
                if (name.equals("success") || name.equals("notfound")) {
                    defaultedServices.add(service);
                } else {
                    cases.add(name);
                    allTestCases.add(name);
                }
            }
            servicesWithCases.put(service, cases);
        }

        for (var testcase : allTestCases.stream().sorted().collect(Collectors.toList())) {
            var applicableServices = servicesWithTestcase(testcase, servicesWithCases, defaultedServices);
            var description = loadDescription(type, testcase);
            result.add(new Testcase(testcase, description, TestcaseType.DOMAIN, applicableServices));
        }

        return result;
    }

    private String loadDescription(String type, String testcase) throws IOException {
        try (var is = finder.loadSimulatorResource(type, testcase + ".txt")) {
            if (is != null) {
                return String.join("\r\n", IOUtils.readLines(is, StandardCharsets.UTF_8));
            }
        }
        return "Testcase " + testcase;
    }

    private List<TestcaseService> servicesWithTestcase(String testcase, HashMap<TestcaseService, Set<String>> servicesWithCases, HashSet<TestcaseService> defaultedServices) {
        var result = new HashSet<TestcaseService>();

        for (var service : servicesWithCases.entrySet()) {
            if (service.getValue().contains(testcase)) {
                result.add(service.getKey());
            }
        }
        result.addAll(defaultedServices);

        return result.stream().sorted(this::compareServices).collect(Collectors.toList());
    }

    private int compareServices(TestcaseService s1, TestcaseService s2) {
        return s1.toString().compareTo(s2.toString());
    }
}
