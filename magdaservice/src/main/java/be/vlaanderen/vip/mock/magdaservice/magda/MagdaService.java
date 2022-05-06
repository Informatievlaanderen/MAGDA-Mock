package be.vlaanderen.vip.mock.magdaservice.magda;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MagdaService {

    private final String serviceNaam;

    private final String serviceVersie;

    public String getUrl() {
        return serviceNaam + "/" + serviceVersie;
    }

}