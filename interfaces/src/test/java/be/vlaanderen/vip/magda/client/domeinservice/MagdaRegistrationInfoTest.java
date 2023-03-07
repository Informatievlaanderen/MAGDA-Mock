package be.vlaanderen.vip.magda.client.domeinservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagdaRegistrationInfoTest {

    @Test
    public void builder_canBuildInstanceWithoutHoedanigheidscode() {
        MagdaRegistrationInfo magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .name("foo")
                .identification("http://foo-uri")
                .build();

        assertEquals("foo", magdaRegistrationInfo.getName());
        assertEquals("http://foo-uri", magdaRegistrationInfo.getIdentification());
        assertNull(magdaRegistrationInfo.getHoedanigheidscode());
    }

    @Test
    public void builder_canBuildInstanceWithHoedanigheidscode() {
        MagdaRegistrationInfo magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .name("foo")
                .identification("http://foo-uri")
                .hoedanigheidscode("1234")
                .build();

        assertEquals("foo", magdaRegistrationInfo.getName());
        assertEquals("http://foo-uri", magdaRegistrationInfo.getIdentification());
        assertEquals("1234", magdaRegistrationInfo.getHoedanigheidscode());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutName() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationInfo.builder()
                        .identification("http://foo-uri")
                        .hoedanigheidscode("1234")
                        .build());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutIdentification() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaRegistrationInfo.builder()
                        .name("foo")
                        .hoedanigheidscode("1234")
                        .build());
    }
}