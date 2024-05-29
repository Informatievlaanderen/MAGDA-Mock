package be.vlaanderen.vip.magda.client.domain.model.socialstatute;

import be.vlaanderen.vip.magda.client.domain.givesocialstatute.SocialStatute;
import be.vlaanderen.vip.magda.client.domain.givesocialstatute.SocialStatutes;
import be.vlaanderen.vip.magda.client.domain.model.shared.OffsetDateXmlAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Persoon")
@Accessors(fluent = true)
@Getter
public class SocialStatutesJaxb  implements SocialStatutes, Serializable {

    @XmlElement(name = "INSZ")
    String insz;

    @XmlElementWrapper(name = "SocialeStatuten")
    @XmlElement(name = "SociaalStatuut")
    ArrayList<SocialStatuteImpl> socialStatutes;

    @Override
    public List<SocialStatute> socialStatutes() {
        if(socialStatutes == null) return Collections.emptyList();
        return socialStatutes.stream()
                .map(SocialStatute.class::cast)
                .toList();
    }

    @Getter
    private static class SocialStatuteImpl implements SocialStatute, Serializable {

        @XmlElement(name = "Naam", nillable = true)
        String name;

        @XmlElement(name = "Resultaat", nillable = true)
        SocialStatutesJaxb.Result result;

        @XmlElementWrapper(name = "Periodes", nillable = true)
        @XmlElement(name = "Periode", nillable = true)
        ArrayList<SocialStatutesJaxb.Period> periods;

        @XmlElement(name = "Status", nillable = true)
        SocialStatutesJaxb.Status status;

        @Override
        public List<SocialStatute.Period> periods() {
            if(periods == null) return Collections.emptyList();
            return periods.stream()
                    .map(period -> (SocialStatute.Period) period)
                    .toList();
        }
    }

    @Getter
    private static class Result implements SocialStatute.Result, Serializable {

        @XmlElement(name = "Code")
        String code;
    }

    @Getter
    private static class Period implements SocialStatute.Period, Serializable {

        @XmlElement(name = "Begindatum")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime startDate;

        @XmlElement(name = "Einddatum", nillable = true)
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime endDate;
    }

    @Getter
    private static class Status implements SocialStatute.Status, Serializable {

        @XmlElement(name = "Code")
        String code;
    }
}
