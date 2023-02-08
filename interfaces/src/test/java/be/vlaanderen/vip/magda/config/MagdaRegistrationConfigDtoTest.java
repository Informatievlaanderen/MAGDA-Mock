package be.vlaanderen.vip.magda.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagdaRegistrationConfigDtoTest {

    @Test
    public void builder_canBuildInstanceWithoutCapacity() {
        MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                .key("foo")
                .uri("http://foo-uri")
                .build();

        assertEquals("foo", dto.getKey());
        assertEquals("http://foo-uri", dto.getUri());
        assertNull(dto.getCapacity());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutKey() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationConfigDto.builder()
                        .uri("http://foo-uri")
                        .capacity("1234")
                        .build());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutUri() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationConfigDto.builder()
                        .key("foo")
                        .capacity("1234")
                        .build());
    }

    @Nested
    class Deprecated {

        @Test
        public void instantiationFromConstructor() {
            MagdaRegistrationConfigDto dto = new MagdaRegistrationConfigDto("foo", "http://foo-uri", "1234");

            assertEquals("foo", dto.getKey());
            assertEquals("http://foo-uri", dto.getUri());
            assertEquals("1234", dto.getCapacity());
        }

        @Test
        public void setters_setAttributes() {
            MagdaRegistrationConfigDto dto = new MagdaRegistrationConfigDto("foo", "http://foo-uri", "1234");
            dto.setKey("bar");
            dto.setUri("http://bar-uri");
            dto.setCapacity("5678");

            assertEquals("bar", dto.getKey());
            assertEquals("http://bar-uri", dto.getUri());
            assertEquals("5678", dto.getCapacity());
        }

        @Test
        public void builder_canBuildInstanceWithAllFields() {
            MagdaRegistrationConfigDto dto = MagdaRegistrationConfigDto.builder()
                    .key("foo")
                    .uri("http://foo-uri")
                    .capacity("1234")
                    .build();

            assertEquals("foo", dto.getKey());
            assertEquals("http://foo-uri", dto.getUri());
            assertEquals("1234", dto.getCapacity());
        }
    }
}