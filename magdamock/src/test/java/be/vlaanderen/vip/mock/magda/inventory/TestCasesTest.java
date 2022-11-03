package be.vlaanderen.vip.mock.magda.inventory;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCasesTest {

    @SneakyThrows
    @Test
    void listsPersoonTestcases() {
        var builder = new TestCaseInventoryBuilder(TestCasesTest.class);
        var personen = builder.persoonTestcases();

        assertThat(personen).hasSize(2);
        Testcase domainTestcase = personen.get(0);
        assertThat(domainTestcase.getKey()).isEqualTo("00000099504");
        assertThat(domainTestcase.getDescription()).isEqualTo("Testcase 00000099504");
        assertThat(domainTestcase.getServices()).hasSize(4);
        assertThat(domainTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefBewijs", "02.00.0000"));
        assertThat(domainTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefPasfoto", "02.00.0000"));
        assertThat(domainTestcase.getServices().get(2)).isEqualTo(new TestcaseService("GeefPersoon", "02.00.0000"));
        assertThat(domainTestcase.getServices().get(3)).isEqualTo(new TestcaseService("GeefPersoon", "02.02.0000"));

        Testcase foutTestcase = personen.get(1);
        assertThat(foutTestcase.getKey()).isEqualTo("91010100144");
        assertThat(foutTestcase.getDescription()).isEqualTo("MAGDA FOUT 99996 - Te veel gelijktijdige bevragingen");
        assertThat(foutTestcase.getServices()).hasSize(3);
        assertThat(foutTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefBewijs", "02.00.0000"));
        assertThat(foutTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefPasfoto", "02.00.0000"));
        assertThat(foutTestcase.getServices().get(2)).isEqualTo(new TestcaseService("GeefPersoon", "02.02.0000"));

    }

    @SneakyThrows
    @Test
    void listsOndernemingTestcases() {
        var builder = new TestCaseInventoryBuilder(TestCasesTest.class);
        var ondernemingen = builder.ondernemingTestcases();

        assertThat(ondernemingen).hasSize(2);
        Testcase domainTestcase = ondernemingen.get(0);
        assertThat(domainTestcase.getKey()).isEqualTo("0242069537");
        assertThat(domainTestcase.getDescription()).isEqualTo("Testcase 0242069537");
        assertThat(domainTestcase.getServices()).hasSize(1);
        assertThat(domainTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefOnderneming", "02.00.0000"));

        Testcase foutTestcase = ondernemingen.get(1);
        assertThat(foutTestcase.getKey()).isEqualTo("0411696011");
        assertThat(foutTestcase.getDescription()).isEqualTo("Test onderneming\r\nHier moet nog wat omschrijving komen");
        assertThat(foutTestcase.getServices()).hasSize(2);
        assertThat(foutTestcase.getServices().get(0)).isEqualTo(new TestcaseService("GeefOnderneming", "02.00.0000"));
        assertThat(foutTestcase.getServices().get(1)).isEqualTo(new TestcaseService("GeefOndernemingVKBO", "02.00.0000"));

    }
}
