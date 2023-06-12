package be.vlaanderen.vip.magda.client.domeinservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagdaRegistrationInfoTest {

    @Test
    void builder_canBuildInstanceWithoutHoedanigheidscode() {
        var magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification("http://foo-uri")
                .build();

        assertEquals("http://foo-uri", magdaRegistrationInfo.getIdentification());
        assertTrue(magdaRegistrationInfo.getHoedanigheidscode().isEmpty());
    }

    @Test
    void builder_canBuildInstanceWithHoedanigheidscode() {
        var magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification("http://foo-uri")
                .hoedanigheidscode("1234")
                .build();

        assertEquals("http://foo-uri", magdaRegistrationInfo.getIdentification());
        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        assertTrue(hoedanigheidscode.isPresent());
        assertEquals("1234", hoedanigheidscode.get());
    }

    @Test
    void builder_cannotBuildInstanceWithoutIdentification() {
        var builder = MagdaRegistrationInfo.builder()
                .hoedanigheidscode("1234");

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}