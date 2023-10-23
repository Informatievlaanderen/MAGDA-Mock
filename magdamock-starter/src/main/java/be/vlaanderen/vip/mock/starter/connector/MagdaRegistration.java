package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MagdaRegistration {
    private String identification;
    private String hoedanigheidscode;
    
    public MagdaRegistrationConfigDto toMagdaRegistrationConfigDto() {
        return MagdaRegistrationConfigDto.builder()
                                         .identification(identification)
                                         .hoedanigheidscode(hoedanigheidscode)
                                         .build();
    }

}
