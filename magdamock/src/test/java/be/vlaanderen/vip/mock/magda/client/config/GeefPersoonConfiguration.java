package be.vlaanderen.vip.mock.magda.client.config;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeefPersoonConfiguration {
    public static final MagdaServiceIdentificatie GEEF_PERSOON_V_0202 = new MagdaServiceIdentificatie("GeefPersoon", "02.02.0000");

    public static final MagdaServiceIdentificatie GEEF_HISTORIEK_PERSOON = new MagdaServiceIdentificatie("GeefHistoriekPersoon", "02.02.0000");

    public GeefPersoonConfiguration(MagdaEndpoints magdaEndpoints) {
        magdaEndpoints.addMapping("GeefPersoon", "02.02.0000", "https://magdapersoondienst.vlaanderen.be/GeefPersoonDienst-02.02/soap/WebService");

        magdaEndpoints.addMapping("GeefHistoriekPersoon", "02.02.0000", "https://magdapersoondienst.vlaanderen.be/GeefHistoriekPersoonDienst-02.02/soap/WebService");
    }
}
