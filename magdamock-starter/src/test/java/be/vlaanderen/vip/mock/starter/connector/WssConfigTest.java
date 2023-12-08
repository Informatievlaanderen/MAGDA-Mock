package be.vlaanderen.vip.mock.starter.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WssConfigTest {

    @Nested
    class GetTwoWaySslProperties {
        
        @Test
        void fromProperties() {
            var config = new WssConfig();
            config.setKeyStorePath("key-store-path");
            config.setKeyStorePassword("key-store-password");
            config.setKeyAlias("key-alias");
            config.setKeyPassword("key-password");
            
            var result = config.getTwoWaySslProperties();
            
            assertAll(
                    () -> assertThat(result.getKeyStoreLocation(), is(equalTo("key-store-path"))),
                    () -> assertThat(result.getKeyAlias(), is(equalTo("key-alias"))),
                    () -> assertThat(result.getKeyPassword(), is(equalTo("key-password"))),
                    () -> assertThat(result.getKeyStorePassword(), is(equalTo("key-store-password"))));
        }
        
    }
    
}
