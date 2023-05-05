package be.vlaanderen.vip.mock.magdaservice.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.TestcaseFinder;
import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;

@Configuration
public class InventoryConfig {
    private ResourceFinder finder;
    
    public InventoryConfig(
            ResourceFinder finder) {
        this.finder = finder;
    }
    
    @Bean
    public TestcaseFinder testcaseFinder() {
        return new TestcaseFinder(finder);
    }
    
    @Bean
    public TestcaseInventory personTestcases() {
        return new TestcaseInventory(testcaseFinder().listServices("Persoon"));
    }

    @Bean
    public TestcaseInventory businesesTestcases() throws IOException {
        return new TestcaseInventory(testcaseFinder().listServices("Onderneming"));
    }
    
}
