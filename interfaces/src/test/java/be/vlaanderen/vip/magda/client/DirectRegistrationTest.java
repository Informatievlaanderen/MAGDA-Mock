package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DirectRegistrationTest {

    private static final MagdaRegistrationInfo INFO = MagdaRegistrationInfo.builder()
            .identification("identification")
            .hoedanigheidscode("hoedanigheidscode")
            .build();

    @Mock
    private MagdaHoedanigheidService magdaHoedanigheidService;

    @Test
    void returnsDirectlyProvidedRegistrationInfo() {
        var directRegistration = new DirectRegistration(INFO);

        assertEquals(INFO, directRegistration.resolve(magdaHoedanigheidService));
        verifyNoInteractions(magdaHoedanigheidService);
    }
}