package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class DirectRegistration implements Registration {

    private final MagdaRegistrationInfo info;

    @Override
    public MagdaRegistrationInfo resolve(MagdaHoedanigheidService magdaHoedanigheidService) {
        return info;
    }
}
