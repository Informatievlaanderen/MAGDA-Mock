package be.vlaanderen.vip.mock.magdaservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;

@RestController
@RequestMapping("/api/v1/testcases")
public class TestcaseController {
    private TestcaseInventory personTestcases;
    private TestcaseInventory businesesTestcases;
    
    public TestcaseController(
            TestcaseInventory personTestcases,
            TestcaseInventory businesesTestcases) {
        this.personTestcases = personTestcases;
        this.businesesTestcases = businesesTestcases;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/persoon",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getPersonTestcases() {
        return new ResponseEntity<>(personTestcases, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/onderneming",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getBusinesesTestcases() {
        return new ResponseEntity<>(businesesTestcases, HttpStatus.OK);
    }
}
