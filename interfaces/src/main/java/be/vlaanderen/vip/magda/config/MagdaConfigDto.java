package be.vlaanderen.vip.magda.config;

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
    private String environment; // PROD | TEST | <base link to mock server>
    private String legalloggingUrl;
    /* TODO: private TwoWaySslProperties keystore;*/
    private Map<String, MagdaRegistrationConfigDto> registration = new HashMap<>();
}
