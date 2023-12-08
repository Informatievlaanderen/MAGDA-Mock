package be.vlaanderen.vip.mock.starter.connector;

import java.util.HashMap;
import java.util.Map;

import org.springframework.vault.core.VaultTemplate;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MagdaConfigBuilder {
    private VaultTemplate vaultTemplate;
    private VaultWssConfig vaultWssConfig;
    private WssConfig wssConfig;
    private Map<String, MagdaRegistrationConfigDto> registrations = new HashMap<>();
    
    private MagdaConfigBuilder() {}
    
    public static MagdaConfigBuilder builder() {
        return new MagdaConfigBuilder();
    }
    
    /**
     * Use keystore stored in vault to sign request sent over magda connection
     */
    public MagdaConfigBuilder vaultWssConfig(VaultTemplate vaultTemplate, VaultWssConfig vaultWssConfig) {
        this.vaultTemplate = vaultTemplate;
        this.vaultWssConfig = vaultWssConfig;
        return this;
    }
    
    public MagdaConfigBuilder wssConfig(WssConfig wssConfig) {
        this.wssConfig = wssConfig;
        return this;
    }
    
    public MagdaConfigBuilder registration(String name, MagdaRegistrationConfigDto registration) {
        log.debug("Adding registration '%s': '%s'".formatted(name, registration));
        this.registrations.put(name, registration);
        return this;
    }
    
    public MagdaConfigBuilder registrations(Map<String, MagdaRegistrationConfigDto> registrations) {
        log.debug("Adding registrations %s".formatted(registrations));
        this.registrations.putAll(registrations);
        return this;
    }
    
    public MagdaConfigBuilder defaultRegistration(MagdaRegistrationConfigDto registration) {
        return registration(MagdaRequest.DEFAULT_REGISTRATION, registration);
    }
    
    public MagdaConfigDto build() {
        if(registrations.isEmpty()) throw new IllegalArgumentException("No registrations are configured");
        
        var keystore = getKeystore();
        
        var config = new MagdaConfigDto();
        config.setKeystore(keystore);
        config.setVerificationEnabled(keystore != null);
        config.setRegistration(registrations);
        return config;
    }
    
    private TwoWaySslProperties getKeystore() {
        if(vaultTemplate != null && vaultWssConfig != null) {
            log.debug("Signing magda connector messages with keystore from vault");
            return vaultWssConfig.getTwoWaySslProperties(vaultTemplate);
        }
        if(wssConfig != null) {
            log.debug("Signing magda connector messages with keystore from filesystem");
            return wssConfig.getTwoWaySslProperties();
        }
        log.debug("Not signing magda connector messages");
        return null;
    }
}
