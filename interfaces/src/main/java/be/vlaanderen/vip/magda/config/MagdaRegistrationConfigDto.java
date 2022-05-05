package be.vlaanderen.vip.magda.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagdaRegistrationConfigDto {
    private String key; // default
    private String uri; // kb.vlaanderen.be/aiv/burgerloket-wwoom-aip
    private String capacity; // 1300
}
