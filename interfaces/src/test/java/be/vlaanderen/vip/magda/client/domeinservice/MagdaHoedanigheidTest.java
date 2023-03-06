package be.vlaanderen.vip.magda.client.domeinservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagdaHoedanigheidTest {

    @Test
    public void builder_canBuildInstanceWithoutHoedanigheidCode() {
        MagdaHoedanigheid magdaHoedanigheid = MagdaHoedanigheid.builder()
                .naam("foo")
                .uri("http://foo-uri")
                .build();

        assertEquals("foo", magdaHoedanigheid.getNaam());
        assertEquals("http://foo-uri", magdaHoedanigheid.getUri());
        assertNull(magdaHoedanigheid.getHoedanigheid());
    }

    @Test
    public void builder_canBuildInstanceWithHoedanigheidCode() {
        MagdaHoedanigheid magdaHoedanigheid = MagdaHoedanigheid.builder()
                .naam("foo")
                .uri("http://foo-uri")
                .hoedanigheid("1234")
                .build();

        assertEquals("foo", magdaHoedanigheid.getNaam());
        assertEquals("http://foo-uri", magdaHoedanigheid.getUri());
        assertEquals("1234", magdaHoedanigheid.getHoedanigheid());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutName() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaHoedanigheid.builder()
                        .uri("http://foo-uri")
                        .hoedanigheid("1234")
                        .build());
    }

    @Test
    public void builder_cannotBuildInstanceWithoutUri() {
        assertThrows(IllegalArgumentException.class, () ->
                MagdaHoedanigheid.builder()
                        .naam("foo")
                        .hoedanigheid("1234")
                        .build());
    }
}