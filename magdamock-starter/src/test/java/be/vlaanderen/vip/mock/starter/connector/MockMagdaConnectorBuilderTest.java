package be.vlaanderen.vip.mock.starter.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MockMagdaConnectorBuilderTest {

    @Nested
    class Build {
        
        @Test
        void createsMagdaConnector() throws URISyntaxException, IOException {
            var result = MagdaConnectorBuilder.builder()
                                              .mock()
                                              .build();
            
            assertThat(result, is(not(nullValue())));
        }
        
    }
    
}
