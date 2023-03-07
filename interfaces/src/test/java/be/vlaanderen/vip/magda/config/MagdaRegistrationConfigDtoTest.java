package be.vlaanderen.vip.magda.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagdaRegistrationConfigDtoTest {

    @Test
    public void builder_canBuildInstanceWithoutHoedanigheidscode() {
        MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                .identification("http://foo-uri")
                .build();

        assertEquals("http://foo-uri", dto.getIdentification());
        assertNull(dto.getHoedanigheidscode());
    }

    @Test
    public void builder_canBuildInstanceWithHoedanigheidscode() {
        MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                .identification("http://foo-uri")
                .hoedanigheidscode("1234")
                .build();

        assertEquals("http://foo-uri", dto.getIdentification());
        assertEquals("1234", dto.getHoedanigheidscode());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutIdentification() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationConfigDto.builder()
                        .build());
    }
}