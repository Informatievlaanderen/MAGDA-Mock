package be.vlaanderen.vip.mock.magda.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestCasesTest {
    @InjectMocks
    private TestCaseInventoryBuilder builder;
    
    @Nested
    class Person {
        private List<Testcase> cases;
        
        @BeforeEach
        void setup() throws IOException {
            cases = builder.persoonTestcases();
        }
        
        @Test
        void listsTestCases() {
            assertThat(cases).hasSize(2);
            Testcase domainTestcase = cases.get(0);
            assertThat(domainTestcase.getKey()).isEqualTo("00000099504");
            assertThat(domainTestcase.getDescription()).isEqualTo("Testcase 00000099504");
            assertThat(domainTestcase.getServices()).hasSize(4);
            assertThat(domainTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefBewijs", "02.00.0000"));
            assertThat(domainTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefPasfoto", "02.00.0000"));
            assertThat(domainTestcase.getServices().get(2)).isEqualTo(new TestcaseService("GeefPersoon", "02.00.0000"));
            assertThat(domainTestcase.getServices().get(3)).isEqualTo(new TestcaseService("GeefPersoon", "02.02.0000"));

            Testcase foutTestcase = cases.get(1);
            assertThat(foutTestcase.getKey()).isEqualTo("91010100144");
            assertThat(foutTestcase.getDescription()).isEqualTo("MAGDA FOUT 99996 - Te veel gelijktijdige bevragingen");
            assertThat(foutTestcase.getServices()).hasSize(3);
            assertThat(foutTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefBewijs", "02.00.0000"));
            assertThat(foutTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefPasfoto", "02.00.0000"));
            assertThat(foutTestcase.getServices().get(2)).isEqualTo(new TestcaseService("GeefPersoon", "02.02.0000"));

        }
    }
    
    @Nested
    class Company {
        private List<Testcase> cases;
        
        @BeforeEach
        void setup() throws IOException {
            cases = builder.ondernemingTestcases();
        }

        @Test
        void listsCases() {
            assertThat(cases).hasSize(2);
            Testcase domainTestcase = cases.get(0);
            assertThat(domainTestcase.getKey()).isEqualTo("0242069537");
            assertThat(domainTestcase.getDescription()).isEqualTo("Testcase 0242069537");
            assertThat(domainTestcase.getServices()).hasSize(1);
            assertThat(domainTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefOnderneming", "02.00.0000"));

            Testcase foutTestcase = cases.get(1);
            assertThat(foutTestcase.getKey()).isEqualTo("0411696011");
            assertThat(foutTestcase.getDescription()).isEqualTo("Test onderneming\r\nHier moet nog wat omschrijving komen");
            assertThat(foutTestcase.getServices()).hasSize(2);
            assertThat(foutTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefOnderneming", "02.00.0000"));
            assertThat(foutTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefOndernemingVKBO", "02.00.0000"));

        }
        
    }

}
