package be.vlaanderen.vip.magda.client.domeinservice;

import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class MagdaHoedanigheidServiceImpl implements MagdaHoedanigheidService {
    private final MagdaConfigDto magdaConfigDto;
    private final String serviceName;

    public MagdaHoedanigheidServiceImpl(MagdaConfigDto magdaConfigDto, @Value("${spring.application.name}") String serviceName) {
        this.magdaConfigDto = magdaConfigDto;
        this.serviceName = serviceName;
    }


    public MagdaHoedanigheid getDomeinService() {
        return getDomeinService("default");
    }

    public MagdaHoedanigheid getDomeinService(String name) {
        if (StringUtils.isEmpty(name)) {
            return getDomeinService();
        }

        if (!magdaConfigDto.getRegistration().containsKey(name)) {
            throw new IllegalStateException("Geen MAGDA registratie gekend in de configuratie met naam '" + name + "'");
        }

        MagdaRegistrationConfigDto registrationConfig = magdaConfigDto.getRegistration().get(name);

        return MagdaHoedanigheid.builder()
                .naam(serviceName)
                .uri(registrationConfig.getUri())
                .hoedanigheid(registrationConfig.getCapacity())
                .build();
    }
}
