package be.vlaanderen.vip.magda.config;

import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagdaConfigDto {
    private String legalloggingUrl;
    private TwoWaySslProperties keystore;
    private boolean verificationEnabled;
    @Builder.Default
    private Map<String, MagdaRegistrationConfigDto> registration = new HashMap<>();
}
