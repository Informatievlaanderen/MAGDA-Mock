package be.vlaanderen.vip.mock.magda.inventory;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class TestcaseInventory {
    private final List<Testcase> testcases;

    public List<Testcase> testcases() {
        return testcases;
    }

}
