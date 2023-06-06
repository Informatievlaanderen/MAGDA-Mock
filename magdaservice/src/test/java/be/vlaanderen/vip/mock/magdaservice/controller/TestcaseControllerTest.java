package be.vlaanderen.vip.mock.magdaservice.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import be.vlaanderen.vip.mock.magda.inventory.TestcaseInventory;

@ExtendWith(MockitoExtension.class)
class TestcaseControllerTest {
    @Mock private TestcaseInventory persoonTestcases;
    @Mock private TestcaseInventory businesesTestcases;

    private TestcaseController controller;
    
    @BeforeEach
    void setup() {
        controller = new TestcaseController(persoonTestcases, businesesTestcases);
    }
    
    @Nested
    class GetPersonTestcases {
        
        @Test
        void isPersonTestCases() {
            var result = controller.getPersonTestcases();
            
            assertAll(
                    () -> assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK))),
                    () -> assertThat(result.getBody(), is(equalTo(persoonTestcases))));
        }
        
    }
    
    @Nested
    class GetBusinesesTestcases {
        
        @Test
        void isPersonTestCases() {
            var result = controller.getBusinesesTestcases();
            
            assertAll(
                    () -> assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK))),
                    () -> assertThat(result.getBody(), is(equalTo(businesesTestcases))));
        }
        
    }
    
}
