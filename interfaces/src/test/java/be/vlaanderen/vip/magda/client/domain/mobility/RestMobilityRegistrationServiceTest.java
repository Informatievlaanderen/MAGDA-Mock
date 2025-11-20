package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.TestHelpers;
import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestMobilityRegistrationServiceTest {
    @Mock
    private MagdaClient magdaRestClient;

    @Nested
    class GetRegistrations {
        @Test
        void testCallsMagdaService() throws Exception {
            RestMobilityRegistrationService service = new RestMobilityRegistrationService(magdaRestClient, new MobilityRegistrationJsonAdapter());

            var request = MobilityRegistrationRequest.builder()
                    .enduserId("00000000097")
                    .registrationInfo(MagdaRegistrationInfo.builder().hoedanigheidscode("HC").identification("ID").build())
                    .plateNr("TXAA999")
                    .build();

            MagdaResponseJson response = new MagdaResponseJson(new ObjectMapper().readTree(TestHelpers.getResourceAsString(getClass(), "/magdamock/mobility/registration-sample.json")), 200);
            when(magdaRestClient.sendRestRequest(any())).thenReturn(response);
            List<Registration> registrationList = service.getRegistrations(request);
            assertEquals(1, registrationList.size());
            assertNotNull(registrationList.get(0));
        }
    }
}
