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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Optional.ofNullable(attesten).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Attest) o).toList();
    }

    @Override
    public List<DmfaAttest.Result> antwoordInBatch() {
        return Optional.ofNullable(antwoordInBatch).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Result) o).toList();
    }

    @Getter
    private static class Attest implements DmfaAttest.Attest, Serializable {
        @XmlElement(name = "Identificatie")
        private Identificatie identificatie;

        @XmlElement(name = "Versie")
        private Versie versie;

        @XmlElement(name = "AangifteWerkgever")
        private AangifteWerkgever aangifteWerkgever;

        @XmlElement(name = "Anomalien")
        private List<Anomalie> anomalien;

        public List<DmfaAttest.Anomalie> anomalien() {
            return Optional.ofNullable(anomalien).stream().map(o -> (DmfaAttest.Anomalie) o).toList();
        }
    }

    @Getter
    private static class Identificatie implements DmfaAttest.Identificatie, Serializable {
        @XmlElement(name = "Versie")
        public String versie;

        @XmlElement(name = "Status")
        public CodeAndDescriptionJaxb status;

        @XmlElement(name = "DatumCreatie")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumCreatie;

        @XmlElement(name = "Nummer")
        private String nummer;
    }

    @Getter
    private static class Versie implements DmfaAttest.Versie, Serializable {
        @XmlElement(name = "Vorige")
        public String vorigeVersie;

        @XmlElement(name = "Volgende")
        public String volgendeVersie;
    }

    @Getter
    private static class AangifteWerkgever implements DmfaAttest.AangifteWerkgever, Serializable {
        @XmlElement(name = "Kwartaal")
        public String kwartaal;

        @XmlElement(name = "RSZNummer")
        public String RSZNummer;

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

        @XmlElement(name = "Naam")
        public Naam naam;

        @XmlElement(name = "Werknemerslijn")
        public Werknemerslijn werknemerslijn;
    }

    @Getter
    private static class Naam implements DmfaAttest.Naam, Serializable {
        @XmlElement(name = "Voornaam")
        public String voornaam;

        @XmlElement(name = "Achternaam")
        public String achternaam;
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
        public String specialeBijdrage;

        @XmlElement(name = "Pensioen")
        public Pensioen pensioen;

        @XmlElement(name = "Staving")
        public Staving staving;

        @XmlElementWrapper(name = "Tewerkstellingen")
        @XmlElement(name = "Tewerkstelling")
        public List<Tewerkstelling> tewerkstellingen;

        @XmlElementWrapper(name = "Bijdragen")
        @XmlElement(name = "Bijdrage")
        public List<Bijdrage> bijdragen;

        @XmlElementWrapper(name = "Verminderingen")
        @XmlElement(name = "Vermindering")
        List<Vermindering> verminderingen;

        @XmlElement(name = "BijzondereBijdrageOntslagenStatutaireWerknemer")
        BijzondereBijdrageOntslagenStatutaireWerknemer bijzondereBijdrageOntslagenStatutaire;

        @XmlElement(name = "BijdrageStudent")
        BijdrageStudent bijdrageStudent;

        @XmlElementWrapper(name = "BijdragenBruggepensioneerde")
        @XmlElement(name = "BijdrageBruggepensioneerde")
        List<BijdrageBruggepensioneerde> bijdragenBruggepensioneerde;

        @XmlElementWrapper(name = "VergoedingenArbeidsongevalBeroepsziekte")
        @XmlElement(name = "VergoedingArbeidsongevalBeroepsziekte")
        List<VergoedingArbeidsongevalBeroepsziekte> vergoedingenArbeidsongevalBeroepsziekte;

        @XmlElementWrapper(name = "AanvullendeVergoedingen")
        @XmlElement(name = "AanvullendeVergoeding")
        List<Vergoeding> aanvullendeVergoedingen;


        public List<DmfaAttest.Tewerkstelling> tewerkstellingen() {
            return Optional.ofNullable(tewerkstellingen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Tewerkstelling) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Bijdrage> bijdragen() {
            return Optional.ofNullable(bijdragen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Bijdrage) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Vermindering> verminderingen() {
            return Optional.ofNullable(verminderingen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Vermindering) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.BijdrageBruggepensioneerde> bijdragenBruggepensioneerde() {
            return Optional.ofNullable(bijdragenBruggepensioneerde).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.BijdrageBruggepensioneerde) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.VergoedingArbeidsongevalBeroepsziekte> vergoedingenArbeidsongevalBeroepsziekte() {
            return Optional.ofNullable(vergoedingenArbeidsongevalBeroepsziekte).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.VergoedingArbeidsongevalBeroepsziekte) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Vergoeding> aanvullendeVergoedingen() {
            return Optional.ofNullable(aanvullendeVergoedingen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Vergoeding) o).collect(Collectors.toList());
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
    private static class Pensioen implements DmfaAttest.Pensioen, Serializable {
        @XmlElement(name = "Bediende")
        public BigDecimal bediende;
        @XmlElement(name = "Arbeider")
        public BigDecimal arbeider;
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
        public String aantalWerkdagenPerWeek;

        @XmlElement(name = "TypeContract")
        public String typeContract;

        @XmlElement(name = "GemiddeldAantalUrenPerWeek")
        public GemiddeldAantalUrenPerWeek gemiddeldAantalUrenPerWeek;

        @XmlElement(name = "Maatregel")
        public MaatRegel maatregel;

        @XmlElement(name = "StatuutWerknemer")
        public String statuutWerknemer;

        @XmlElement(name = "Gepensioneerd")
        public Integer gepensioneerd;

        @XmlElement(name = "TypeLeerling")
        public String typeLeerling;

        @XmlElement(name = "WijzeBezoldiging")
        public String wijzeBezoldiging;

        @XmlElement(name = "Functienummer")
        public String functienummer;

        @XmlElement(name = "KlasseVliegendPersoneel")
        public String klasseVliegendPersoneel;

        @XmlElement(name = "BetalingIn10denOf12den")
        public String betalingIn10denOf12den;

        @XmlElement(name = "StavingDagen")
        public String stavingDagen;

        @XmlElement(name = "Uurloon")
        public String uurloon;

        @XmlElement(name = "PercentageVermindering")
        public String percentageVermindering;

        @XmlElement(name = "Personeelklasse")
        public Integer personeelklasse;

        @XmlElement(name = "GemiddeldAantalGesubsidieerdeUren")
        public Integer gemiddeldAantalGesubsidieerdeUren;

        @XmlElement(name = "Versie")
        public String versie;

        @XmlElement(name = "RegionalisatieDoelgroepVermindering")
        public String regionalisatieDoelgroepVermindering;

        @XmlElement(name = "GemeenteNISCode")
        public String gemeenteNISCode;

        @XmlElement(name = "Informatie")
        public Informatie informatie;

        @XmlElementWrapper(name = "Prestaties")
        @XmlElement(name = "Prestatie")
        public List<Prestatie> prestaties;

        @XmlElementWrapper(name = "Bezoldigingen")
        @XmlElement(name = "Bezoldiging")
        public List<Bezoldiging> bezoldigingen;

        @XmlElementWrapper(name = "Overheidssectoren")
        @XmlElement(name = "Overheidssector")
        public List<Overheidssector> overheidssectoren;

        @XmlElementWrapper(name = "ReorganisatiesArbeidstijdInfo")
        @XmlElement(name = "ReorganisatieArbeidstijdInfo")
        public List<ReorganisatieArbeidstijdInfo> reorganisatiesArbeidstijdInfo;

        @XmlElement(name = "TweedepijlerInformatie")
        public TweedepijlerInformatie tweedepijlerInformatie;

        @XmlElementWrapper(name = "Verminderingen")
        @XmlElement(name = "Vermindering")
        public List<Vermindering> verminderingen;

        @XmlElement(name = "GebruikendeOnderneming")
        private GebruikendeOnderneming gebruikendeOnderneming;

        public List<DmfaAttest.Prestatie> prestaties() {
            return Optional.ofNullable(prestaties).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Prestatie) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Bezoldiging> bezoldigingen() {
            return Optional.ofNullable(bezoldigingen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Bezoldiging) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Overheidssector> overheidssectoren() {
            return Optional.ofNullable(overheidssectoren).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Overheidssector) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.ReorganisatieArbeidstijdInfo> reorganisatiesArbeidstijdInfo() {
            return Optional.ofNullable(reorganisatiesArbeidstijdInfo).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.ReorganisatieArbeidstijdInfo) o).collect(Collectors.toList());
        }

        public List<DmfaAttest.Vermindering> verminderingen() {
            return Optional.ofNullable(verminderingen).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Vermindering) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class LokaleEenheid implements DmfaAttest.LokaleEenheid, Serializable {
        @XmlElement(name = "Nummer")
        public String nummer;

        @XmlElement(name = "NISCode")
        public String nisCode;
    }

    @Getter
    private static class GemiddeldAantalUrenPerWeek implements DmfaAttest.GemiddeldAantalUrenPerWeek, Serializable {
        @XmlElement(name = "Referentiepersoon")
        public String referentiePersoon;

        @XmlElement(name = "Werknemer")
        public String werkNemer;
    }

    @Getter
    private static class MaatRegel implements DmfaAttest.MaatRegel, Serializable {
        @XmlElement(name = "ReorganisatieArbeidstijd")
        private String reorganisatieArbeidstijd;

        @XmlElement(name = "BevorderingWerkgelegenheid")
        private String bevorderingWerkgelegenheid;
    }

    @Getter
    public static class Informatie implements DmfaAttest.Informatie, Serializable {
        @XmlElement(name = "DatumZesMaandenZiek")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumZesMaandenZiek;

        @XmlElement(name = "HorecaExtra")
        public String horecaExtra;

        @XmlElement(name = "Terbeschikkingstelling")
        public String terbeschikkingstelling;

        @XmlElement(name = "UurloonInDuizendsten")
        public String uurloonInDuizendsten;

        @XmlElement(name = "GewaardborgdloonEersteWeek")
        public String gewaardborgdloonEersteWeek;

        @XmlElement(name = "BrutoLoonZiekte")
        public BigDecimal brutoLoonZiekte;

        @XmlElement(name = "Vrijstelling")
        public Vrijstelling vrijstelling;

        @XmlElement(name = "DatumVasteBenoeming")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumVasteBenoeming;

        @XmlElement(name = "Versie")
        public String versie;

        @XmlElement(name = "Loopbaanmaatregel")
        public Integer loopbaanmaatregel;

        @XmlElement(name = "Aantalvakantiedagen")
        public Integer aantalvakantiedagen;

        @XmlElement(name = "Sectordetail")
        public String sectordetail;

        @XmlElement(name = "Mobiliteitsbudget")
        public Long mobiliteitsbudget;

        @XmlElement(name = "AantalUrenVlaamsOpleidingsverlof")
        public Integer aantalUrenVlaamsOpleidingsverlof;

        @XmlElement(name = "RegionaleSteunmaatregel")
        public String regionaleSteunmaatregel;
    }

    @Getter
    public static class Vrijstelling implements DmfaAttest.Vrijstelling, Serializable {
        @XmlElement(name = "VanAangifteGegevensTewerkstellingPSD")
        public String vanAangifteGegevensTewerkstellingPSD;

        @XmlElement(name = "VanBijdrageAanvullendPensioen")
        public String vanBijdrageAanvullendPensioen;
    }

    @Getter
    private static class Prestatie implements DmfaAttest.Prestatie, Serializable {
        @XmlElement(name = "Volgnummer")
        public String volgnummer;

        @XmlElement(name = "Code")
        public String code;

        @XmlElement(name = "Dagen")
        public String dagen;
    }

    @Getter
    private static class Bezoldiging implements DmfaAttest.Bezoldiging, Serializable {
        @XmlElement(name = "Volgnummer")
        private String volgnummer;

        @XmlElement(name = "Code")
        private String code;

        @XmlElement(name = "FrequentieBetalingPremie")
        private String frequentieBetalingPremie;

        @XmlElement(name = "PercentageJaarbasis")
        private String percentageJaarbasis;

        @XmlElement(name = "FictiefSalaris")
        private Long fictiefSalaris;

        @XmlElement(name = "Bedrag")
        private Long bedrag;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class Overheidssector implements DmfaAttest.Overheidssector, Serializable {
        @XmlElement(name = "Periode")
        private Periode periode;

        @XmlElement(name = "TypeInstelling")
        private String typeInstelling;

        @XmlElement(name = "TypePersoneel")
        private String typePersoneel;

        @XmlElement(name = "GraadOfFunctie")
        private String graadOfFunctie;

        @XmlElement(name = "OfficieleTaal")
        private String officieleTaal;

        @XmlElement(name = "TypeOpdracht")
        private String typeOpdracht;

        @XmlElement(name = "AardFunctie")
        private String aardFunctie;

        @XmlElement(name = "RedenEindeStatutaireRelatie")
        private Integer redenEindeStatutaireRelatie;

        @XmlElement(name = "Versie")
        private String versie;

        @XmlElementWrapper(name = "Loonschalen")
        @XmlElement(name = "Loonschaal")
        private List<Loonschaal> loonschalen;

        public List<DmfaAttest.Loonschaal> loonschalen() {
            return loonschalen.stream().map(o -> (DmfaAttest.Loonschaal) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class Loonschaal implements DmfaAttest.Loonschaal, Serializable {
        @XmlElement(name = "Periode")
        private Periode periode;

        @XmlElement(name = "StartAncienniteit")
        private String startAncienniteit;

        @XmlElement(name = "Verwijzing")
        private String verwijzing;

        @XmlElement(name = "Bedrag")
        private Long bedrag;

        @XmlElement(name = "UrenPerWeek")
        private String urenPerWeek;

        @XmlElement(name = "UurloonPerWeek")
        private String uurloonPerWeek;

        @XmlElement(name = "VerminderdeLoonschaalNotificatie")
        private String verminderdeLoonschaalNotificatie;

        @XmlElement(name = "Versie")
        private String versie;

        @XmlElementWrapper(name = "AanvullendeLoonschalen")
        @XmlElement(name = "AanvullendeLoonschaal")
        private List<AanvullendeLoonschaal> aanvullendeLoonschalen;

        public List<DmfaAttest.AanvullendeLoonschaal> aanvullendeLoonschalen() {
            return aanvullendeLoonschalen.stream().map(o -> (DmfaAttest.AanvullendeLoonschaal) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class AanvullendeLoonschaal implements DmfaAttest.AanvullendeLoonschaal, Serializable {
        @XmlElement(name = "Periode")
        private Periode periode;

        @XmlElement(name = "Verwijzing")
        private String verwijzing;

        @XmlElement(name = "Basisloon")
        private Long basisloon;

        @XmlElement(name = "Percentage")
        private String percentage;

        @XmlElement(name = "AantalDiensturen")
        private Integer aantalDiensturen;

        @XmlElement(name = "Bedrag")
        private Long bedrag;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class ReorganisatieArbeidstijdInfo implements DmfaAttest.ReorganisatieArbeidstijdInfo, Serializable {
        @XmlElement(name = "ReorganisatieArbeidstijd")
        private String reorganisatieArbeidstijd;

        @XmlElement(name = "PercentageReorganisatieArbeidstijd")
        private String percentageReorganisatieArbeidstijd;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class TweedepijlerInformatie implements DmfaAttest.TweedepijlerInformatie, Serializable {
        @XmlElement(name = "ReferentieJaarMaand")
        private String referentieJaarMaand;

        @XmlElement(name = "MaandBedrag")
        private Long maandBedrag;

        @XmlElement(name = "BijkomendMaandBedrag")
        private Long bijkomendMaandbedrag;

        @XmlElement(name = "MaandelijkseHuisvergoeding")
        private Long maandelijkseHuisvergoeding;

        @XmlElement(name = "OfficieleTaal")
        private Integer officieleTaal;

        @XmlElement(name = "Versie")
        private String versie;
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
    private static class Vermindering implements DmfaAttest.Vermindering, Serializable {
        @XmlElement(name = "Code")
        public String code;

        @XmlElement(name = "BasisVoorBerekening")
        public Long basisVoorBerekening;

        @XmlElement(name = "BedragVermindering")
        public Long bedragVermindering;

        @XmlElement(name = "DatumBeginRecht")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        public LocalDate datumBeginRecht;

        @XmlElement(name = "AantalMaandenBeheerskostRSZ")
        public String aantalMaandenBeheerskostRSZ;

        @XmlElement(name = "VervangenINSZ")
        public String vervangenINSZ;

        @XmlElement(name = "INSZRechtOpenend")
        public String INSZRechtOpenend;

        @XmlElement(name = "HerkomstAttest")
        public String herkomstAttest;

        @XmlElement(name = "Versie")
        public String versie;

        @XmlElementWrapper(name = "Details")
        @XmlElement(name = "Detail")
        private List<Detail> details;

        @Override
        public List<DmfaAttest.Detail> details() {
            return Optional.ofNullable(details).stream().flatMap(Collection::stream).map(o -> (DmfaAttest.Detail) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class Detail implements DmfaAttest.Detail, Serializable {
        @XmlElement(name = "Volgnummer")
        private String volgnummer;

        @XmlElement(name = "Bedrag")
        private BigDecimal bedrag;

        @XmlElement(name = "NummerRegistratieArbeidsreglement")
        private String nummerRegistratieArbeidsreglement;

        @XmlElement(name = "DatumBeginArbeidsregeling")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        private LocalDate datumBeginArbeidsregeling;

        @XmlElement(name = "GemiddeldeArbeidsDuur")
        private GemiddeldeArbeidsDuur gemiddeldeArbeidsduur;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class GemiddeldeArbeidsDuur implements DmfaAttest.GemiddeldeArbeidsDuur, Serializable {
        @XmlElement(name = "VoorVermindering")
        private BigDecimal voorVermindering;

        @XmlElement(name = "NaVermindering")
        private BigDecimal naVermindering;
    }

    @Getter
    private static class BijzondereBijdrageOntslagenStatutaireWerknemer implements DmfaAttest.BijzondereBijdrageOntslagenStatutaireWerknemer, Serializable {
        @XmlElement(name = "ReferentieBrutoloon")
        private Integer referentieBrutoloon;

        @XmlElement(name = "ReferentieBrutoloonBijdrage")
        private Long referentieBrutoloonBijdrage;

        @XmlElement(name = "ReferentieAantalDagen")
        private String referentieAantalDagen;

        @XmlElement(name = "PeriodeOnderwerping")
        private Periode periodeOnderwerping;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class BijdrageStudent implements DmfaAttest.BijdrageStudent, Serializable {
        @XmlElement(name = "Loon")
        private Integer loon;

        @XmlElement(name = "Bedrag")
        private BigDecimal bedrag;

        @XmlElement(name = "AantalDagen")
        private String aantalDagen;

        @XmlElement(name = "AantalUren")
        private String aantalUren;

        @XmlElement(name = "NummerLokaleEenheid")
        private String nummerLokaleEenheid;

        @XmlElementWrapper(name = "GebruikendeOndermingen")
        @XmlElement(name = "GebruikendeOnderneming")
        private List<GebruikendeOnderneming> gebruikendeOndernemingen;

        @XmlElement(name = "Versie")
        private String versie;

        @Override
        public List<DmfaAttest.GebruikendeOnderneming> gebruikendeOndernemingen() {
            return gebruikendeOndernemingen.stream().map(o -> ((DmfaAttest.GebruikendeOnderneming) o)).collect(Collectors.toList());
        }
    }

    @Getter
    private static class GebruikendeOnderneming implements DmfaAttest.GebruikendeOnderneming, Serializable {
        @XmlElement(name = "INSZ")
        private String INSZ;

        @XmlElement(name = "Ondernemingsnummer")
        private String ondernemingsnummer;

        @XmlElement(name = "NaamOnderneming")
        private String naamOnderneming;

        @XmlElement(name = "BTWNummer")
        private String BTWnummer;

        @XmlElement(name = "DagContractNummer")
        private String dagContractNummer;

        @XmlElement(name = "Adres")
        private Adres adres;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class Adres implements DmfaAttest.Adres, Serializable {
        @XmlElement(name = "Straat")
        private Straat straat;

        @XmlElement(name = "Huisnummer")
        private String huisnummer;

        @XmlElement(name = "Busnummer")
        private String busnummer;

        @XmlElement(name = "Gemeente")
        private Gemeente gemeente;

        @XmlElement(name = "Land")
        private Land land;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class Straat implements DmfaAttest.Straat, Serializable {
        @XmlElement(name = "Code")
        private String code;

        @XmlElement(name = "Naam")
        private String naam;
    }

    @Getter
    private static class Gemeente implements DmfaAttest.Gemeente, Serializable {
        @XmlElement(name = "NISCode")
        private String NISCode;

        @XmlElement(name = "PostCode")
        private String postCode;

        @XmlElement(name = "Naam")
        private String naam;
    }

    @Getter
    private static class Land implements DmfaAttest.Land, Serializable {
        @XmlElement(name = "NISCode")
        private String NISCode;

        @XmlElement(name = "ISOCode")
        private String ISOCode;

        @XmlElement(name = "Naam")
        private String naam;
    }

    @Getter
    private static class BijdrageBruggepensioneerde implements DmfaAttest.BijdrageBruggepensioneerde, Serializable {
        @XmlElement(name = "Code")
        private String code;

        @XmlElement(name = "AantalMaanden")
        private String aantalMaanden;

        @XmlElement(name = "Bedrag")
        private Long bedrag;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class VergoedingArbeidsongevalBeroepsziekte implements DmfaAttest.VergoedingArbeidsongevalBeroepsziekte, Serializable {
        @XmlElement(name = "Reden")
        private String reden;

        @XmlElement(name = "GraadArbeidsOngeschikdheid")
        private String graadArbeidsOngeschikdheid;

        @XmlElement(name = "Bedrag")
        private BigDecimal bedrag;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class Vergoeding implements DmfaAttest.Vergoeding, Serializable {
        @XmlElement(name = "Basis")
        private VergoedingBasis basis;

        @XmlElementWrapper(name = "Bijdragen")
        @XmlElement(name = "Bijdrage")
        private List<Bijdrage> bijdragen;

        public List<DmfaAttest.VergoedingBijdrage> bijdragen() {
            return Optional.ofNullable(bijdragen).stream().map(o -> (DmfaAttest.VergoedingBijdrage) o).collect(Collectors.toList());
        }
    }

    @Getter
    private static class VergoedingBasis implements DmfaAttest.VergoedingBasis, Serializable {
        @XmlElement(name = "IdentificatieWerkgever")
        private String identificatieWerkgever;

        @XmlElement(name = "ParitairComite")
        private String paritairComite;

        @XmlElement(name = "Activiteit")
        private CodeAndDescriptionJaxb activiteit;

        @XmlElement(name = "TypeSchuldenaar")
        private String typeSchuldenaar;

        @XmlElement(name = "DatumEersteVergoeding")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        private LocalDate datumEersteVergoeding;

        @XmlElement(name = "TypeAkkoord")
        private String typeAkkoord;

        @XmlElement(name = "HalftijdseLoopbaanonderbreking")
        private String halftijdseLoopbaanonderbreking;

        @XmlElement(name = "VrijstellingPrestaties")
        private String vrijstellingPrestaties;

        @XmlElement(name = "VervangingInOvereenkomstMetCAO")
        private String vervangingInOvereenkomstMetCAO;

        @XmlElement(name = "Vervanger")
        private String vervanger;

        @XmlElement(name = "RegelingBijWerkhervatting")
        private String regelingBijWerkhervatting;

        @XmlElement(name = "AantalOnderdelenSchadevergoeding")
        private String aantalOnderdelenSchadevergoeding;

        @XmlElement(name = "DatumBetekeningOpzegging")
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        private LocalDate datumBetekeningOpzegging;

        @XmlElement(name = "OndernemingInMoeilijkheden")
        private String ondernemingInMoeilijkheden;

        @XmlElement(name = "PeriodeErkenning")
        private Periode periodeErkenning;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class VergoedingBijdrage implements DmfaAttest.VergoedingBijdrage, Serializable {
        @XmlElement(name = "WerknemerKengetal")
        private String werknemerKengetal;

        @XmlElement(name = "Type")
        private String type;

        @XmlElement(name = "BedragModificatie")
        private Integer bedragModificatie;

        @XmlElement(name = "Volgnummer")
        private String volgnummer;

        @XmlElement(name = "Vergoeding")
        private Integer vergoeding;

        @XmlElement(name = "NotieKapitalisatie")
        private String notieKapitalisatie;

        @XmlElement(name = "TheoretischBedragBijdstanduitkering")
        private Integer theoretischBedragBijstanduitkering;

        @XmlElement(name = "AantalMaanden")
        private String aantalMaanden;

        @XmlElement(name = "AantalMaandenDecimaal")
        private String aantalMaandenDecimaal;

        @XmlElement(name = "Onvolledigemaand")
        private OnvolledigeMaand onvolledigeMaand;

        @XmlElement(name = "ToepassingVanDeDrempel")
        private String toepassingVanDeDrempel;

        @XmlElement(name = "Bedrag")
        private Integer bedrag;

        @XmlElement(name = "Versie")
        private String versie;
    }

    @Getter
    private static class OnvolledigeMaand implements DmfaAttest.OnvolledigeMaand, Serializable {
        @XmlElement(name = "AantalDagen")
        BigDecimal aantalDagen;

        @XmlElement(name = "Reden")
        String reden;
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
