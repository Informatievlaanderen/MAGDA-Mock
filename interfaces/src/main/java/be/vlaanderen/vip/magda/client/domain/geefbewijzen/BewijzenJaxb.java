package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.domain.model.shared.MonthXmlAdapter;
import be.vlaanderen.vip.magda.client.domain.model.shared.YearXmlAdapter;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.vip.magda.client.domain.geefbewijzen.Bewijs.*;

@XmlRootElement(name = "Inhoud")
@Accessors(fluent = true)
@Getter
public class BewijzenJaxb implements Bewijzen, Serializable {

    @Serial
    private static final long serialVersionUID = -714593557838815590L;

    @XmlElementWrapper(name = "Bewijzen")
    @XmlElement(name = "Bewijs")
    ArrayList<BewijsJaxb> bewijzen;

    @Override
    public List<Bewijs> bewijzen() {
        return bewijzen.stream()
                .map(x -> (Bewijs) x)
                .toList();
    }

    @Getter
    private static class BewijsJaxb implements Bewijs, Serializable {

        @Serial
        private static final long serialVersionUID = -8619077685449167361L;

        @XmlElement(name = "BewijsrefertesLed")
        BewijsrefertesLedJaxb bewijsrefertesled;

        @XmlElement(name = "Leverancier")
        NaamJaxb leverancier;

        @XmlElement(name = "Afgeleid")
        AfgeleidJaxb afgeleid;

        @XmlElement(name = "Basis")
        BasisJaxb basis;
    }

    @Getter
    private static class BewijsrefertesLedJaxb implements BewijsrefertesLed, Serializable {

        @Serial
        private static final long serialVersionUID = 7871290888321087998L;

        @XmlElement(name = "Bewijsreferte")
        String bewijsreferte;
    }

    @Getter
    private static class AfgeleidJaxb implements Afgeleid, Serializable {

        @Serial
        private static final long serialVersionUID = -798688696802777877L;

        @XmlElement(name = "ISCEDNiveau")
        CodeEnOptioneleNaamJaxb iscedNiveau;

        @XmlElement(name = "ISCEDStudiegebied")
        CodeEnNaamJaxb iscedStudiegebied;

        @XmlElement(name = "VKSNiveauOnderwijskwalificatie")
        CodeEnNaamJaxb vksNiveauOnderwijskwalificatie;

        @XmlElement(name = "VKSNiveauBeroepskwalificatie")
        CodeEnNaamJaxb vksNiveauBeroepskwalificatie;
    }

    @Getter
    private static class AlternatieveInstantieJaxb implements AlternatieveInstantie, Serializable {

        @Serial
        private static final long serialVersionUID = -8928018348109913431L;

        @XmlElement(name = "Instantierol")
        NaamJaxb instantierol;

        @XmlElement(name = "Instantie")
        NaamJaxb instantie;
    }

    @Getter
    private static class BasisJaxb implements Basis, Serializable {

        @Serial
        private static final long serialVersionUID = 2038089676881615361L;

        @XmlElementWrapper(name = "AlternatieveInstanties")
        @XmlElement(name = "AlternatieveInstantie")
        @Nullable
        ArrayList<AlternatieveInstantieJaxb> alternatieveInstanties;

        @Override
        public List<AlternatieveInstantie> alternatieveInstanties() {
            if(alternatieveInstanties != null) {
                return alternatieveInstanties.stream()
                        .map(x -> (AlternatieveInstantie) x)
                        .toList();
            } else {
                return List.of();
            }
        }

        @XmlElement(name = "Authenticiteit")
        NaamJaxb authenticiteit;

        @XmlElement(name = "Bewijsstaat")
        NaamJaxb bewijsstaat;

        @XmlElement(name = "Bewijstype")
        NaamJaxb bewijstype;

        @XmlElementWrapper(name = "BijkomendeInformaties")
        @XmlElement(name = "BijkomendeInformatie")
        @Nullable
        ArrayList<BijkomendeInformatieJaxb> bijkomendeInformaties;

        @Override
        public List<BijkomendeInformatie> bijkomendeInformaties() {
            if(bijkomendeInformaties != null) {
                return bijkomendeInformaties.stream()
                        .map(x -> (BijkomendeInformatie) x)
                        .toList();
            } else {
                return List.of();
            }
        }

        @XmlElement(name = "Categorie")
        NaamJaxb categorie;

        @XmlElement(name = "Detailonderwerp")
        @Nullable
        NaamEnOptioneleCodeJaxb detailOnderwerp;

        @XmlElement(name = "Graad")
        NaamJaxb graad;

        @XmlElement(name = "Instantie")
        NaamJaxb instantie;

        @XmlElement(name = "Instelling")
        InstellingJaxb instelling;

        @XmlElement(name = "Land")
        CodeJaxb land;

        @XmlElement(name = "Onderwerp")
        CodeEnOptioneleNaamJaxb onderwerp;

        @XmlElement(name = "Onderwijsvorm")
        NaamJaxb onderwijsvorm;

        @XmlElement(name = "Schooltype")
        NaamJaxb schooltype;

        @XmlElement(name = "Specialisatie")
        @Nullable
        NaamEnOptioneleCodeJaxb specialisatie;

        @XmlElement(name = "Studierichting")
        @Nullable
        NaamEnOptioneleCodeJaxb studierichting;

        @XmlElement(name = "Taal")
        CodeJaxb taal;

        @XmlElement(name = "VervalPeriode")
        @Nullable
        String vervalperiode;

        @XmlElement(name = "VolledigeNaam")
        String volledigeNaam;

        @XmlElement(name = "Uitreikingsdatum")
        UitreikingsdatumJaxb uitreikingsdatum;

        @XmlElement(name = "UrenVolwassenenonderwijs")
        @Nullable
        Integer urenVolwassenenonderwijs;
    }

    @Getter
    private static class BijkomendeInformatieJaxb implements BijkomendeInformatie, Serializable {

        @Serial
        private static final long serialVersionUID = -6375801628001523092L;

        @XmlElement(name = "Naam")
        String naam;

        @XmlElement(name = "Inhoud")
        String inhoud;
    }

    @Getter
    private static class InstellingJaxb implements Instelling, Serializable {

        @Serial
        private static final long serialVersionUID = 5051261903744557422L;

        @XmlElement(name = "Naam")
        String naam;

        @XmlElement(name = "Nummer")
        @Nullable
        String nummer;
    }

    @Getter
    private static class UitreikingsdatumJaxb implements Uitreikingsdatum, Serializable {

        @Serial
        private static final long serialVersionUID = -8619912429705697203L;

        @XmlElement(name = "Jaar")
        @XmlJavaTypeAdapter(YearXmlAdapter.class)
        Year jaar;

        @XmlElement(name = "Maand")
        @XmlJavaTypeAdapter(MonthXmlAdapter.class)
        @Nullable
        Month maand;

        @XmlElement(name = "Dag")
        @Nullable
        Integer dag;
    }

    @Getter
    private static class CodeJaxb implements Code, Serializable {

        @Serial
        private static final long serialVersionUID = 4618937655150696766L;

        @XmlElement(name = "Code")
        String code;
    }

    @Getter
    private static class NaamJaxb implements Naam, Serializable {

        @Serial
        private static final long serialVersionUID = -275994521707904966L;

        @XmlElement(name = "Naam")
        String naam;
    }

    @Getter
    private static class CodeEnNaamJaxb implements CodeEnNaam, Serializable {

        @Serial
        private static final long serialVersionUID = 100924777482987602L;

        @XmlElement(name = "Code")
        String code;

        @XmlElement(name = "Naam")
        String naam;
    }

    @Getter
    private static class CodeEnOptioneleNaamJaxb implements CodeEnOptioneleNaam, Serializable {

        @Serial
        private static final long serialVersionUID = 100924777482987602L;

        @XmlElement(name = "Code")
        String code;

        @XmlElement(name = "Naam")
        @Nullable
        String naam;
    }

    @Getter
    private static class NaamEnOptioneleCodeJaxb implements NaamEnOptioneleCode, Serializable {

        @Serial
        private static final long serialVersionUID = 100924777482987602L;

        @XmlElement(name = "Code")
        @Nullable
        String code;

        @XmlElement(name = "Naam")
        String naam;
    }
}
