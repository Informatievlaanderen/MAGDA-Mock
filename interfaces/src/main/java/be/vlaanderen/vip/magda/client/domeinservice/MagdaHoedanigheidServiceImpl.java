package be.vlaanderen.vip.magda.client.domeinservice;

import org.apache.commons.lang3.StringUtils;

import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;

public class MagdaHoedanigheidServiceImpl implements MagdaHoedanigheidService {
    private final MagdaConfigDto magdaConfigDto;

    public MagdaHoedanigheidServiceImpl(MagdaConfigDto magdaConfigDto) {
        this.magdaConfigDto = magdaConfigDto;
    }

    @Override
    public MagdaRegistrationInfo getDomeinService(String name) {
        if (StringUtils.isEmpty(name)) {
            return getDomeinService("default");
        }

        if (!magdaConfigDto.getRegistration().containsKey(name)) {
            throw new IllegalStateException("Geen MAGDA registratie gekend in de configuratie met naam '" + name + "'");
        }

        MagdaRegistrationConfigDto registrationConfig = magdaConfigDto.getRegistration().get(name);

        return MagdaRegistrationInfo.builder()
                .identification(registrationConfig.getIdentification())
                .hoedanigheidscode(registrationConfig.getHoedanigheidscode())
                .build();
    }
}
