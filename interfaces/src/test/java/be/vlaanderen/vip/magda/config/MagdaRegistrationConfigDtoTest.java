package be.vlaanderen.vip.magda.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagdaRegistrationConfigDtoTest {

    @Test
    void builder_canBuildInstanceWithoutHoedanigheidscode() {
        var dto = MagdaRegistrationConfigDto.builder()
                .identification("http://foo-uri")
                .build();

        assertEquals("http://foo-uri", dto.getIdentification());
        assertTrue(dto.getHoedanigheidscode().isEmpty());
    }

    @Test
    void builder_canBuildInstanceWithHoedanigheidscode() {
        var dto = MagdaRegistrationConfigDto.builder()
                .identification("http://foo-uri")
                .hoedanigheidscode("1234")
                .build();

        assertEquals("http://foo-uri", dto.getIdentification());
        var hoedanigheidscode = dto.getHoedanigheidscode();
        assertTrue(hoedanigheidscode.isPresent());
        assertEquals("1234", hoedanigheidscode.get());
    }

    @Test
    void builder_cannotBuildInstanceWithoutIdentification() {
        var builder = MagdaRegistrationConfigDto.builder();

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}