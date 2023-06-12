package be.vlaanderen.vip.mock.magdaservice.controller;

import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/testcases")
public class TestcaseController {
    private final TestcaseInventory personTestcases;
    private final TestcaseInventory businesesTestcases;
    
    public TestcaseController(
            TestcaseInventory personTestcases,
            TestcaseInventory businesesTestcases) {
        this.personTestcases = personTestcases;
        this.businesesTestcases = businesesTestcases;
    }

    @GetMapping(
            value = "/persoon",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getPersonTestcases() {
        return new ResponseEntity<>(personTestcases, HttpStatus.OK);
    }

    @GetMapping(
            value = "/onderneming",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getBusinesesTestcases() {
        return new ResponseEntity<>(businesesTestcases, HttpStatus.OK);
    }
}
