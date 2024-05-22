package be.vlaanderen.vip.magda.client.domain.model.arza;

import be.vlaanderen.vip.magda.client.domain.givearzacareer.ARZACareer;
import be.vlaanderen.vip.magda.client.domain.shared.CodeAndDescriptionJaxb;
import be.vlaanderen.vip.magda.client.domain.shared.OffsetDateXmlAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Zelfstandige")
@Accessors(fluent = true)
public class ARZACareerJaxb implements ARZACareer, Serializable {

    @XmlElement(name = "Ondernemingsnummer")
    @Getter
    String enterpriseNumber;

    @XmlElementWrapper(name = "Aansluitingen")
    @XmlElement(name = "Aansluiting")
    ArrayList<Affiliation> affiliations;

    @Override
    public List<ARZACareer.Affiliation> affiliations() {
        return affiliations.stream()
                .map(affiliation -> (ARZACareer.Affiliation) affiliation)
                .toList();
    }

    private static class Affiliation implements ARZACareer.Affiliation, Serializable {

        @XmlElement(name = "SociaalVerzekeringsfonds")
        private SocialInsuranceFund socialInsuranceFund;

        @XmlElementWrapper(name = "Loopbanen")
        @XmlElement(name = "Loopbaan")
        private List<Career> careers;

        @Override
        public String socialInsuranceFundEnterpriseNumber() {
            return socialInsuranceFund.enterpriseNumber;
        }

        @Override
        public List<ARZACareer.Career> careers() {
            return careers.stream()
                    .map(career -> (ARZACareer.Career) career)
                    .toList();
        }

        private static class SocialInsuranceFund implements Serializable {
            @XmlElement(name = "Ondernemingsnummer")
            String enterpriseNumber;
        }

        @Getter
        private static class Career implements ARZACareer.Career, Serializable {

            @XmlElement(name = "Bijdragereeks")
            CodeAndDescriptionJaxb contributionSeries;

            @XmlElement(name = "Periode")
            Period period;

            @XmlElement(name = "PeriodeGelijkgesteld")
            CodeAndDescriptionJaxb periodEquated;

            @XmlElement(name = "Beroep")
            CodeAndDescriptionJaxb profession;

            @XmlElement(name = "Hoedanigheid")
            CodeAndDescriptionJaxb capacity;

            @XmlElement(name = "DatumOndertekening")
            @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
            OffsetDateTime dateOfSignature;

            @Getter
            private static class Period implements ARZACareer.Period, Serializable {

                @XmlElement(name = "Begin")
                @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
                OffsetDateTime startDate;

                @XmlElement(name = "Einde")
                @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
                OffsetDateTime endDate;

            }
        }

    }
}