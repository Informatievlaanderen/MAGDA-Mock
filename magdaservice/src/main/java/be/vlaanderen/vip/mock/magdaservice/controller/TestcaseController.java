package be.vlaanderen.vip.mock.magdaservice.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;
import be.vlaanderen.vip.mock.magdaservice.config.InventoryConfig;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/testcases")
@RequiredArgsConstructor
public class TestcaseController {
    private final InventoryConfig inventoryConfig;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/persoon",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getPersoonTestcases() {
        try {
            return new ResponseEntity<>(inventoryConfig.persoonTestcases(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/onderneming",
            produces = {"application/json"}
    )
    public ResponseEntity<TestcaseInventory> getOndernemingTestcases() {
        try {
            return new ResponseEntity<>(inventoryConfig.ondernemingTestcases(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
