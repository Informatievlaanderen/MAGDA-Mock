package be.vlaanderen.vip.magda.client.domain.model.dmfa;

import be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer.DmfaAttest;
import be.vlaanderen.vip.magda.client.domain.model.shared.CodeAndDescriptionJaxb;
import be.vlaanderen.vip.magda.client.domain.model.shared.LocalDateXmlAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "Inhoud")
@Accessors(fluent = true)
public class DmfaAttestJaxb implements DmfaAttest, Serializable {

    @XmlElementWrapper(name = "Attesten")
    @XmlElement(name = "Attest")
    ArrayList<DmfaAttestJaxb.Attest> attesten;

    @XmlElement(name = "VolgendAttest")
    @Getter
    String volgendAttest;

    @XmlElement(name = "AntwoordInBatch")
    List<Result> antwoordInBatch;

    @Override
    public List<DmfaAttest.Attest> attesten() {
        return attesten.stream().map(o -> (DmfaAttest.Attest) o).toList();
    }

    @Override
    public List<DmfaAttest.Result> antwoordInBatch() {
        return antwoordInBatch.stream().map(o -> (DmfaAttest.Result) o).toList();
    }

    @Getter
    private static class Attest implements DmfaAttest.Attest, Serializable {
        @XmlElement(name = "Identificatie")
        private Identification identificatie;
        @XmlElement(name = "Versie")
        private Versie versie;
        @XmlElement(name = "AangifteWerkgever")
        private AangifteWerkgever aangifteWerkgever;
        @XmlElement(name = "Anomalien")
        private List<Anomalie> anomalien;

        public List<DmfaAttest.Anomalie> anomalien() {
            return anomalien.stream().map(o -> (DmfaAttest.Anomalie) o).toList();
        }
    }

    @Getter
    private static class Identification implements DmfaAttest.Identification, Serializable {
        @XmlElement(name = "Versie")
        public String versie;
        @XmlElement(name = "Status")
        public CodeAndDescriptionJaxb status;
        @XmlElement(name = "DatumCreatie")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate dateumCreatie;
        @XmlElement(name = "Nummer")
        private String nummer;
    }

    @Getter
    private static class Versie implements DmfaAttest.Versie, Serializable {
        @XmlElement(name = "vorige")
        public String vorigeVersie;
        @XmlElement(name = "volgende")
        public String volgendeVersie;
    }

    @Getter
    private static class AangifteWerkgever implements DmfaAttest.AangifteWerkgever, Serializable {
        @XmlElement(name = "Kwartaal")
        public String kwartaal;
        @XmlElement(name = "RSZNummer")
        public String rSZNummer;
        @XmlElement(name = "VorigRSZNummer")
        public String vorigRSZNummer;
        @XmlElement(name = "Bron")
        public String bron;
        @XmlElement(name = "SectorIndicator")
        public String sectorIndicator;
        @XmlElement(name = "OnderCuratele")
        public String onderCuratele;
        @XmlElement(name = "Ondernemingsnummer")
        public String ondernemingsNummer;
        @XmlElement(name = "VerschuldigdNettoBedrag")
        public Integer verschuldigdNettoBedrag;
        @XmlElement(name = "ConversieNaarRegime5")
        public String conversieNaarRegime5;
        @XmlElement(name = "DatumBeginVakantie")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumBeginVakantie;
        @XmlElement(name = "Versie")
        public String versie;
        @XmlElement(name = "Kwaliteit")
        public String kwaliteit;
        @XmlElement(name = "AangeverID")
        public String aangeverID;
        @XmlElement(name = "Staving")
        public Staving staving;
        @XmlElement(name = "Werknemer")
        public Werknemer werknemer;
    }

    @Getter
    private static class Staving extends CodeAndDescriptionJaxb implements DmfaAttest.Staving, Serializable {
        @XmlElement(name = "Datum")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datum;
    }

    @Getter
    private static class Werknemer implements DmfaAttest.Werknemer, Serializable {
        @XmlElement(name = "INSZ")
        public String insz;
        @XmlElement(name = "OriolusValidatie")
        public String oriolusValidatie;
        @XmlElement(name = "VorigINSZ")
        public String vorigINSZ;
        @XmlElement(name = "naam")
        public Name naam;
        @XmlElement(name = "Werknemerslijn")
        public Werknemerslijn werknemerslijn;
    }

    @Getter
    private static class Werknemerslijn implements DmfaAttest.Werknemerslijn, Serializable {
        @XmlElement(name = "Volgnummer")
        public String volgnummer;
        @XmlElement(name = "Categorie")
        public String categorie;
        @XmlElement(name = "Kerngetal")
        public String kernGetal;
        @XmlElement(name = "Periode")
        public Periode periode;
        @XmlElement(name = "Grensarbeider")
        public String grensarbeider;
        @XmlElement(name = "ActiviteitIvmRisico")
        public String activiteitIvmRisico;
        @XmlElement(name = "NummerLokaleEenheid")
        public String nummerLokaleEenheid;
        @XmlElement(name = "SpecialeBijdrage")
        public Double specialeBijdrage;
        @XmlElement(name = "Pensioen")
        public Pension pensioen;
        @XmlElement(name = "Staving")
        public Staving staving;
        @XmlElementWrapper(name = "Tewerkstellingen")
        @XmlElement(name = "Tewerkstelling")
        public List<Tewerkstelling> tewerkstellingen;
        @XmlElementWrapper(name = "Bijdragen")
        @XmlElement(name = "Bijdrag")
        public List<Bijdrage> bijdragen;

        public List<DmfaAttest.Tewerkstelling> tewerkstellingen() {
            return tewerkstellingen.stream().map(o -> (DmfaAttest.Tewerkstelling) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Bijdrage> bijdragen() {
            return bijdragen.stream().map(o -> (DmfaAttest.Bijdrage) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class Periode implements DmfaAttest.Periode, Serializable {
        @XmlElement(name = "Begin")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate begin;

        @XmlElement(name = "Einde")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        LocalDate einde;
    }

    @Getter
    private static class Pension implements DmfaAttest.Pension, Serializable {
        @XmlElement(name = "Bediende")
        public Double bediende;
        @XmlElement(name = "Arbeider")
        public Double arbeider;
    }

    @Getter
    private static class Name implements DmfaAttest.Name, Serializable {
        @XmlElement(name = "Voornaam")
        public String voornaam;
        @XmlElement(name = "Achternaam")
        public String achternaam;
    }

    @Getter
    private static class Result implements DmfaAttest.Result, Serializable {
        @XmlElementWrapper(name = "Resultaten")
        @XmlElement(name = "Resultaat")
        public List<CodeAndDescriptionJaxb> resultaten;

        public List<CodeAndDescription> resultaten() {
            return resultaten.stream().map(o -> (CodeAndDescription) o).toList();
        }
    }

    @Getter
    private static class Tewerkstelling implements DmfaAttest.Tewerkstelling, Serializable {
        @XmlElement(name = "Volgnummer")
        public String volgnummer;
        @XmlElement(name = "InternNummer")
        public String internNummer;
        @XmlElement(name = "LokaleEenheid")
        public LokaleEenheid lokaleEenheid;
        @XmlElement(name = "Activiteit")
        public String activiteit;
        @XmlElement(name = "Periode")
        public Periode periode;
        @XmlElement(name = "ParitairComite")
        public String paritairComite;
        @XmlElement(name = "AantalWerkdagenPerWeek")
        public Double aantalWerkdagenPerWeek;
        @XmlElement(name = "TypeContract")
        public String typeContract;
        @XmlElement(name = "GemiddeldAantalUrenPerWeek")
        public GemiddeldAantalUrenPerWeek gemiddeldAantalUrenPerWeek;
        @XmlElementWrapper(name = "Prestaties")
        @XmlElement(name = "Prestatie")
        public List<Prestatie> prestaties;
        @XmlElement(name = "Informatie")
        public Informatie infomatie;

        public List<DmfaAttest.Prestatie> prestaties() {
            return prestaties.stream().map(o -> (DmfaAttest.Prestatie) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class GemiddeldAantalUrenPerWeek implements DmfaAttest.GemiddeldAantalUrenPerWeek, Serializable {
        @XmlElement(name = "Referentiepersoon")
        public Double referentiePersoon;
        @XmlElement(name = "Werknemer")
        public Double werkNemer;
    }

    @Getter
    private static class Prestatie implements DmfaAttest.Prestatie, Serializable {
        @XmlElement(name = "Volgnummer")
        public String volgnummer;
        @XmlElement(name = "Code")
        public String code;
        @XmlElement(name = "Dagen")
        public Integer dagen;
    }

    @Getter
    private static class LokaleEenheid implements DmfaAttest.LokaleEenheid, Serializable {
        @XmlElement(name = "Nummer")
        public String nummer;
        @XmlElement(name = "NISCode")
        public String nisCode;
    }

    @Getter
    public static class Informatie implements DmfaAttest.Informatie, Serializable {
        @XmlElement(name = "BrutoLoonZiekte")
        public Double brutoLoonZiekte;
    }

    @Getter
    public static class Bijdrage implements DmfaAttest.Bijdrage, Serializable {
        @XmlElement(name = "Code")
        public String code;
        @XmlElement(name = "Type")
        public String type;
        @XmlElement(name = "BasisVoorBerekening")
        public Long basisVoorBerekening;
        @XmlElement(name = "Amount")
        public Long bedrag;
        @XmlElement(name = "Versie")
        public String versie;
    }

    @Getter
    private static class Anomalie implements DmfaAttest.Anomalie, Serializable {
        @XmlElement(name = "Bloknummer")
        public String bloknummer;
        @XmlElement(name = "Lijnnummer")
        public String lijnnummer;
        @XmlElement(name = "Nummer")
        public String nummer;
        @XmlElement(name = "Status")
        public String status;
        @XmlElement(name = "Versie")
        public String versie;
        @XmlElement(name = "DatumCreatie")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumCreatie;
        @XmlElement(name = "DatumBehandeling")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumBehandeling;
    }
}
