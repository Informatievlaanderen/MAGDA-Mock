package be.vlaanderen.vip.magda.legallogging.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UitzonderingEntryTest {

    @Test
    void toString_producesExpectedOutput() {
        var uitzonderingEntry = UitzonderingEntry.builder()
                .identification("identification")
                .origin("origin")
                .diagnosis("diagnosis")
                .uitzonderingType(UitzonderingType.WAARSCHUWING)
                .annotatieFields(List.of(
                        AnnotatieField.builder()
                                .name("name1")
                                .value("value1")
                                .build(),
                        AnnotatieField.builder()
                                .name("name2")
                                .value("value2")
                                .build()))
                .build();

        assertEquals("WAARSCHUWING origin-identification 'diagnosis'", uitzonderingEntry.toString());
    }

    @Test
    void toString_handlesNullValues() {
        var uitzonderingEntry = UitzonderingEntry.builder()
                .build();

        assertEquals("null null-null 'null'", uitzonderingEntry.toString());
    }
}