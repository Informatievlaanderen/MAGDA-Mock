package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RemoteMagdaConnectionBuilderTest {

    @Nested
    class Build {

        @Mock private MagdaConfigDto magdaConfig;
        @Mock private MagdaEndpoints endpoints;

        @Test
        void throwsException_whenConfigMissing() {
            var builder = MagdaConnectorBuilder.builder()
                    .remote()
                    .magdaEndpoints(endpoints);

            assertThrows(IllegalArgumentException.class, builder::build);
        }

        @Test
        void throwsException_whenEndpointsMissing() {
            var builder = MagdaConnectorBuilder.builder()
                    .remote()
                    .magdaConfig(magdaConfig);

            assertThrows(IllegalArgumentException.class, builder::build);
        }
        
        @Test
        void createConnector() throws TwoWaySslException {
            var result = MagdaConnectorBuilder.builder()
                                              .remote()
                                              .magdaConfig(magdaConfig)
                                              .magdaEndpoints(endpoints)
                                              .build();
            
            assertThat(result, is(not(nullValue())));
        }
        
    }
}
