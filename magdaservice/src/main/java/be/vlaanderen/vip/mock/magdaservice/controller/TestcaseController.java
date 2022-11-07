package be.vlaanderen.vip.mock.magdaservice.controller;

import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;
import be.vlaanderen.vip.mock.magdaservice.config.InventoryConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/testcases")
@RequiredArgsConstructor
public class TestcaseController {
    private final InventoryConfig inventoryConfig;

    private final ObjectMapper objectMapper;

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
