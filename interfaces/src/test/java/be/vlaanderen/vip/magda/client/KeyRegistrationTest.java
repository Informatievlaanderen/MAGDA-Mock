package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyRegistrationTest {

    private static final MagdaRegistrationInfo INFO = MagdaRegistrationInfo.builder()
            .identification("identification")
            .hoedanigheidscode("hoedanigheidscode")
            .build();

    @Mock
    private MagdaHoedanigheidService magdaHoedanigheidService;

    @Test
    void fetchesRegistrationInfoFromHoedanigheidsServiceByKey() {
        when(magdaHoedanigheidService.getDomeinService("key"))
                .thenReturn(INFO);

        var keyRegistration = new KeyRegistration("key");

        assertEquals(INFO, keyRegistration.resolve(magdaHoedanigheidService));
    }
}