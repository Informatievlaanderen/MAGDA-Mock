package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.TestHelpers;
import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MobilityRegistrationJsonAdapterTest {
    private final MobilityRegistrationJsonAdapter adapter = new MobilityRegistrationJsonAdapter();

    @Test
    public void whenAdaptingResponseFromValidJson_returnsFilledList() throws Exception {
        MagdaResponseJson response = new MagdaResponseJson(new ObjectMapper().readTree(TestHelpers.getResourceAsString(getClass(), "/magdamock/mobility/registration-sample.json")));
        List<Registration> registrationList = adapter.adapt(response);
        assertEquals(1, registrationList.size());
        assertNotNull(registrationList.get(0));
    }

    @Test
    public void whenAdaptingResponseFromInvalidJson_returnsEmptyList() throws Exception {
        MagdaResponseJson response = new MagdaResponseJson(new ObjectMapper().readTree(TestHelpers.getResourceAsString(getClass(), "/magdamock/mobility/registration-error.json")));
        List<Registration> registrationList = adapter.adapt(response);
        assertEquals(0, registrationList.size());
    }
}
