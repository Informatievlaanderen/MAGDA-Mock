package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredINSZTest {

    @Test
    void toString_returnsInszValue() {
        assertEquals("46061400105", new RegisteredINSZ<>(INSZ.of("46061400105")).toString());
    }
}