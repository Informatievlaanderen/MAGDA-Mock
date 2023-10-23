package be.vlaanderen.vip.mock.starter.connector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultWssConfigTest {
    private VaultWssConfig config;
    
    @BeforeEach
    void setup() {
        config = new VaultWssConfig();
        config.setPath("path");
        config.setKeyStoreKey("key-store-key");
        config.setKeyStorePasswordKey("key-store-password-key");
        config.setKeyAliasKey("key-alias");
        config.setKeyPasswordKey("key-password-key");
    }

    @Nested
    class GetTwoWaySslProperties {
        @Mock private VaultTemplate template;
        
        @Mock private VaultResponse response;
        
        @BeforeEach
        void setup() {
            when(template.read("path")).thenReturn(response);
        }
        
        @Nested
        class VaultDataMissing {
            
            @Test
            void throwsException() {
                when(response.getData()).thenReturn(null);
                
                assertThrows(IllegalArgumentException.class, 
                             () -> config.getTwoWaySslProperties(template));
            }
            
        }
        
        @Nested
        class HasVaultData {
            private final Map<String, Object> responseData = new HashMap<>();
            private final Map<String, Object> data = new HashMap<>();

            @BeforeEach
            void setup() {
                data.put("key-store-key", Base64.getEncoder().encodeToString("keystore-data".getBytes()));
                data.put("key-store-password-key", "keystore-password");
                data.put("key-alias", "alias-of-the-key");
                data.put("key-password-key", "key-password");

                responseData.put("data", data);
                when(response.getData()).thenReturn(responseData);
            }
            
            @Test
            void keystoreIsFileWithDataContents() throws FileNotFoundException, IOException {
                var result = config.getTwoWaySslProperties(template);
                
                try(var file = new FileInputStream(result.getKeyStoreLocation())) {
                    var contents = new String(file.readAllBytes());
                    
                    assertThat(contents, is(equalTo("keystore-data")));
                }
            }
            
            @Test
            void setsProperties_fromData() {
                var result = config.getTwoWaySslProperties(template);
                
                assertAll(
                        () -> assertThat(result.getKeyAlias(), is(equalTo("alias-of-the-key"))),
                        () -> assertThat(result.getKeyStorePassword(), is(equalTo("keystore-password"))),
                        () -> assertThat(result.getKeyPassword(), is(equalTo("key-password"))));
            }
            
            @Test
            void throwsIllegalArgumentException_whenKeystoreNotBase64() {
                data.put("key-store-key", "not-base64");
             
                assertThrows(IllegalArgumentException.class, 
                             () -> config.getTwoWaySslProperties(template));
            }

            @ParameterizedTest
            @ValueSource(strings = {"key-store-key", "key-store-password-key", "key-alias", "key-password-key"})
            void throwsIllegalArgumentException_whenFieldsAreMissing(String field) {
                data.remove(field);
             
                assertThrows(IllegalArgumentException.class, 
                             () -> config.getTwoWaySslProperties(template));
            }
        }
        
    }
}
