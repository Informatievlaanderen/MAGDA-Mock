package be.vlaanderen.vip.magda.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagdaRegistrationConfigDtoTest {

    @Test
    public void builder_canBuildInstanceWithoutCapacity() {
        MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                .uri("http://foo-uri")
                .build();

        assertEquals("http://foo-uri", dto.getUri());
        assertNull(dto.getCapacity());
    }

    @Test
    public void builder_canBuildInstanceWithHoedanigheidscode() {
        MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                .uri("http://foo-uri")
                .capacity("1234")
                .build();

        assertEquals("http://foo-uri", dto.getUri());
        assertEquals("1234", dto.getCapacity());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutUri() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationConfigDto.builder()
                        .build());
    }
}