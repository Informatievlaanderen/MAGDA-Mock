package be.vlaanderen.vip.mock.starter.connector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import lombok.Data;

@Data
public class VaultWssConfig {
    private String path;
    private String keyStoreKey;
    private String keyStorePasswordKey;
    private String keyAliasKey;
    private String keyPasswordKey;
    
    public TwoWaySslProperties getTwoWaySslProperties(VaultTemplate template) {
        var data = getVaultData(template);
        
        var properties = new TwoWaySslProperties();
        properties.setKeyStoreLocation(getKeyStoreLocation(data));
        properties.setKeyAlias(getDataField(data, keyAliasKey));
        properties.setKeyPassword(getDataField(data, keyPasswordKey));
        properties.setKeyStorePassword(getDataField(data, keyStorePasswordKey));
        return properties;
    }
    
    private String getDataField(Map<String, Object> data, String key) {
        return Optional.ofNullable(data.get(key))
                       .map(Object::toString)
                       .orElseThrow(() -> new IllegalArgumentException("Missing key from vault data: '%s'".formatted(key)));
    }
    
    private String getKeyStoreLocation(Map<String, Object> data) {
        var keystore = readKeystore(data);
        var file = saveKeyStore(keystore);
        return file.getAbsolutePath();
    }
    
    private File saveKeyStore(InputStream is) {
        try {
            var file = File.createTempFile("magda-wss-keystore-", ".jks");
            file.deleteOnExit();
            FileUtils.copyInputStreamToFile(is, file);
            return file;
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Failed to save vault keystore in filesystem", e);
        }
    }
    
    private InputStream readKeystore(Map<String, Object> data) {
        return Optional.ofNullable(getDataField(data, keyStoreKey))
                       .map(Object::toString)
                       .map(this::fromBase64)
                       .map(ByteArrayInputStream::new)
                       .orElseThrow(() -> new IllegalArgumentException("Failed to read base64 encoded keystore in vault for key '%s'".formatted(keyStoreKey)));
    }
    
    private byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getVaultData(VaultTemplate template) {
        return Optional.ofNullable(template.read(path))
                       .map(VaultResponse::getData)
                       .map(m -> (Map<String, Object>) m.get("data"))
                       .orElseThrow(() -> new IllegalArgumentException("No wss config found in vault for path '%s'".formatted(path)));
    }
}
