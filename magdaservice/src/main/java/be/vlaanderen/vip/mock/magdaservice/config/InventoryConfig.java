package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.mock.magda.inventory.TestCaseInventoryBuilder;
import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class InventoryConfig {
    @Bean
    public TestcaseInventory persoonTestcases() throws IOException {
        var builder = new TestCaseInventoryBuilder(InventoryConfig.class);
        return new TestcaseInventory(builder.persoonTestcases());
    }

    @Bean
    public TestcaseInventory ondernemingTestcases() throws IOException {
        var builder = new TestCaseInventoryBuilder(InventoryConfig.class);
        return new TestcaseInventory(builder.ondernemingTestcases());
    }
}
