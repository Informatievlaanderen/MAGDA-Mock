package be.vlaanderen.vip.mock.magda.inventory;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Testcase {
    private final String key;
    private final String description;
    private final TestcaseType type;
    private final List<TestcaseService> services;
}
