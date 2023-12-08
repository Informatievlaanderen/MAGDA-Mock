package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagdaConfigBuilderTest {
    
    @Nested
    class Build {
        @Mock private VaultTemplate vaultTemplate;
        @Mock private VaultWssConfig vaultWssConfig;
        @Mock private WssConfig wssConfig;

        @Mock private TwoWaySslProperties keystore;
        
        @Test
        void throwException_whenRegistrationsEmpty() {
            var builder = MagdaConfigBuilder.builder();

            assertThrows(IllegalArgumentException.class, builder::build);
        }
        
        @Test
        void addsDefaultRegistration() {
            var registration = registration();
            
            var result = MagdaConfigBuilder.builder()
                                           .defaultRegistration(registration)
                                           .build();
            
            
            assertAll(
                    () -> assertThat(result.getRegistration(), aMapWithSize(1)),
                    () -> assertThat(result.getRegistration(), hasEntry(MagdaRequest.DEFAULT_REGISTRATION, registration)));
        }
        
        @Test
        void addsRegistrationsForName() {
            var registration1 = registration();
            var registration2 = registration();
            
            var result = MagdaConfigBuilder.builder()
                                           .registration("reg-1", registration1)
                                           .registration("reg-2", registration2)
                                           .build();

            assertAll(
                    () -> assertThat(result.getRegistration(), aMapWithSize(2)),
                    () -> assertThat(result.getRegistration(), hasEntry("reg-1", registration1)),
                    () -> assertThat(result.getRegistration(), hasEntry("reg-2", registration2)));
        }
        
        @Test
        void addsVaultSslProperties_whenVaultConfigPresent() {
            when(vaultWssConfig.getTwoWaySslProperties(vaultTemplate)).thenReturn(keystore);

            var result = MagdaConfigBuilder.builder()
                                           .vaultWssConfig(vaultTemplate, vaultWssConfig)
                                           .defaultRegistration(registration())
                                           .build();
            
            assertAll(
                    () -> assertThat(result.isVerificationEnabled(), is(true)),
                    () -> assertThat(result.getKeystore(), is(equalTo(keystore))));
        }
        
        @Test
        void addsSslProperties_whenConfigPresent() {
            when(wssConfig.getTwoWaySslProperties()).thenReturn(keystore);

            var result = MagdaConfigBuilder.builder()
                                           .wssConfig(wssConfig)
                                           .defaultRegistration(registration())
                                           .build();
            
            assertAll(
                    () -> assertThat(result.isVerificationEnabled(), is(true)),
                    () -> assertThat(result.getKeystore(), is(equalTo(keystore))));
        }
        
        @Test
        void hasNoSslProperties_whenNoWssConfig() {
            var result = MagdaConfigBuilder.builder()
                                           .defaultRegistration(registration())
                                           .build();
            
            assertAll(
                    () -> assertThat(result.isVerificationEnabled(), is(false)),
                    () -> assertThat(result.getKeystore(), is(nullValue())));
        }
        
        private MagdaRegistrationConfigDto registration() {
            return mock(MagdaRegistrationConfigDto.class);
        }
    }
}
