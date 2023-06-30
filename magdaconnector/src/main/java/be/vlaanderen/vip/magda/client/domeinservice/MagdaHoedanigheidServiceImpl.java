package be.vlaanderen.vip.magda.client.domeinservice;

import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import org.apache.commons.lang3.StringUtils;

public class MagdaHoedanigheidServiceImpl implements MagdaHoedanigheidService {
    private final MagdaConfigDto magdaConfigDto;

    public MagdaHoedanigheidServiceImpl(MagdaConfigDto magdaConfigDto) {
        this.magdaConfigDto = magdaConfigDto;
    }

    @Override
    public MagdaRegistrationInfo getDomeinService(String registration) {
        if (StringUtils.isEmpty(registration)) {
            return getDomeinService("default");
        }

        if (!magdaConfigDto.getRegistration().containsKey(registration)) {
            throw new IllegalStateException("No known MAGDA registration in configuration with name '%s'".formatted(registration));
        }

        var registrationConfig = magdaConfigDto.getRegistration().get(registration);

        return MagdaRegistrationInfo.builder()
                .identification(registrationConfig.getIdentification())
                .hoedanigheidscode(registrationConfig.getHoedanigheidscode().orElse(null))
                .build();
    }
}
