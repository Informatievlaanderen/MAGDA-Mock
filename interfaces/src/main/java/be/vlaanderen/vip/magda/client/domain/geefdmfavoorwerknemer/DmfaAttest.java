package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface DmfaAttest {

    static DmfaAttest ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return new MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    List<Attest> attesten();

    String volgendAttest();

    List<Result> antwoordInBatch();

    interface Attest {
        Identificatie identificatie();
        Versie versie();
        AangifteWerkgever aangifteWerkgever();
        List<Anomalie> anomalien();
    }

    interface Identificatie {
        String nummer();
        String versie();
        CodeAndDescription status();
        LocalDate datumCreatie();
    }

    interface Versie{
        String vorigeVersie();
        String volgendeVersie();
    }

    interface AangifteWerkgever{
        String kwartaal();
        String RSZNummer();
        String vorigRSZNummer();
        String bron();
        String sectorIndicator();
        String onderCuratele();
        String ondernemingsNummer();
        Integer verschuldigdNettoBedrag();
        String conversieNaarRegime5();
        LocalDate datumBeginVakantie();
        String versie();
        String kwaliteit();
        String aangeverID();
        Staving staving();
        Werknemer werknemer();
    }

    interface CodeAndDescription {
        String codeValue();
        String codeDescription();
        String descriptionValue();
        String descriptionOrigin();
        String descriptionLanguageCode();
    }

    interface Staving extends CodeAndDescription {
        LocalDate datum();
    }

    interface Werknemer {
        String insz();
        String oriolusValidatie();
        String vorigINSZ();
        Naam naam();
        Werknemerslijn werknemerslijn();
    }

    interface Naam {
        String voornaam();
        String achternaam();
    }

    interface Werknemerslijn {
        String categorie();
        String kernGetal();
        Periode periode();
        String grensarbeider();
        String activiteitIvmRisico();
        String nummerLokaleEenheid();
        BigDecimal specialeBijdrage();
        Pensioen pensioen();
        Staving staving();
        List<Tewerkstelling> tewerkstellingen();
        List<Bijdrage> bijdragen();
        List<Vermindering> verminderingen();
        BijzondereBijdrageOntslagenStatutaireWerknemer bijzondereBijdrageOntslagenStatutaire();
        BijdrageStudent bijdrageStudent();
        List<BijdrageBruggepensioneerde> bijdragenBruggepensioneerde();
        List<VergoedingArbeidsongevalBeroepsziekte> vergoedingenArbeidsongevalBeroepsziekte();
        List<Vergoeding> aanvullendeVergoedingen();
    }

    interface Periode {
        LocalDate begin();
        LocalDate einde();
    }

    interface Pensioen {
        BigDecimal bediende();
        BigDecimal arbeider();
    }

    interface Tewerkstelling {
        String volgnummer();
        String internNummer();
        LokaleEenheid lokaleEenheid();
        String activiteit();
        Periode periode();
        String paritairComite();
        BigDecimal aantalWerkdagenPerWeek();
        String typeContract();
        GemiddeldAantalUrenPerWeek gemiddeldAantalUrenPerWeek();
        MaatRegel maatregel();
        String statuutWerknemer();
        Integer gepensioneerd();
        String typeLeerling();
        String wijzeBezoldiging();
        String functienummer();
        String klasseVliegendPersoneel();
        String betalingIn10denOf12den();
        String stavingDagen();
        BigDecimal uurloon();
        BigDecimal percentageVermindering();
        String personeelklasse();
        String gemiddeldAantalGesubsidieerdeUren();
        String versie();
        String regionalisatieDoelgroepVermindering();
        String gemeenteNISCode();
        Informatie informatie();
        List<Prestatie> prestaties();
        List<Bezoldiging> bezoldigingen();
        List<Overheidssector> overheidssectoren();
        List<ReorganisatieArbeidstijdInfo> reorganisatiesArbeidstijdInfo();
        TweedepijlerInformatie tweedepijlerInformatie();
        GebruikendeOnderneming gebruikendeOnderneming();
        List<Vermindering> verminderingen();
    }

    interface LokaleEenheid {
        String nummer();
        String nisCode();
    }

    interface GemiddeldAantalUrenPerWeek {
        BigDecimal referentiePersoon();
        BigDecimal werkNemer();
    }

    interface MaatRegel {
        String reorganisatieArbeidstijd();
        String bevorderingWerkgelegenheid();
    }

    interface Informatie {
        BigDecimal brutoLoonZiekte();
    }

    interface Prestatie {
        String volgnummer();
        String code();
        Integer dagen();
    }

    interface Bezoldiging {
        String volgnummer();
        String code();
        String frequentieBetalingPremie();
        Integer percentageJaarbasis();
        BigDecimal fictiefSalaris();
        BigInteger bedrag();
        String versie();
    }

    interface Overheidssector {
        Periode periode();
        String typeInstelling();
        String typePersoneel();
        String graadOfFunctie();
        String officieleTaal();
        String typeOpdracht();
        String aardFunctie();
        String redenEindeStatutaireRelatie();
        String versie();
        List<Loonschaal> loonschalen();
    }

    interface Loonschaal {
        Periode periode();
        String startAncienniteit();
        String verwijzing();
        BigDecimal bedrag();
        BigDecimal urenPerWeek();
        BigDecimal uurloonPerWeek();
        String versie();
        List<AanvullendeLoonschaal> aanvullendeLoonschalen();
    }

    interface AanvullendeLoonschaal {
        Periode periode();
        String verwijzing();
        BigDecimal basisloon();
        BigDecimal percentage();
        BigDecimal aantalDiensturen();
        BigDecimal bedrag();
        String versie();
    }

    interface ReorganisatieArbeidstijdInfo {
        String reorganisatieArbeidstijd();
        BigDecimal percentageReorganisatieArbeidstijd();
        String versie();
    }

    interface TweedepijlerInformatie {
        String referentieJaarMaand();
        BigDecimal maandBedrag();
        BigDecimal bijkomendMaandbedrag();
        BigDecimal maandelijkseHuisvergoeding();
        String officieleTaal();
        String versie();
    }

    interface Bijdrage {
        String code();
        String type();
        Long basisVoorBerekening();
        Long bedrag();
        String versie();
    }

    interface Vermindering {
        String code();
        BigDecimal basisVoorBerekening();
        BigDecimal bedragVermindering();
        LocalDate datumBeginRecht();
        BigDecimal aantalMaandenBeheerskostRSZ();
        String vervangenINSZ();
        String INSZRechtOpenend();
        String herkomstAttest();
        String versie();
        List<Detail> details();
    }

    interface Detail {
        String volgnummer();
        BigDecimal bedrag();
        String nummerRegistratieArbeidsreglement();
        LocalDate datumBeginArbeidsregeling();
        GemiddeldeArbeidsDuur gemiddeldeArbeidsduur();
        String versie();
    }

    interface GemiddeldeArbeidsDuur {
        BigDecimal voorVermindering();
        BigDecimal naVermindering();
    }

    interface BijzondereBijdrageOntslagenStatutaireWerknemer {
        BigDecimal referentieBrutoloon();
        BigDecimal referentieBrutoloonBijdrage();
        Integer referentieAantalDagen();
        Periode periodeOnderwerping();
        String versie();
    }

    interface BijdrageStudent {
        BigDecimal loon();
        BigDecimal bedrag();
        Integer aantalDagen();
        Integer aantalUren();
        String nummerLokaleEenheid();
        List<GebruikendeOnderneming> gebruikendeOndernemingen();
        String versie();
    }

    interface GebruikendeOnderneming {
        String INSZ();
        String ondernemingsnummer();
        String naamOnderneming();
        String BTWnummer();
        String dagContractNummer();
        Adres adres();
        String versie();
    }

    interface Adres {
        Straat straat();
        String huisnummer();
        String busnummer();
        Gemeente gemeente();
        Land land();
        String versie();
    }

    interface Straat {
        String code();
        String naam();
    }

    interface Gemeente {
        String NISCode();
        String naam();
        String postCode();
    }

    interface Land {
        String NISCode();
        String ISOCode();
        String naam();
    }

    interface BijdrageBruggepensioneerde {
        String code();
        Integer aantalMaanden();
        BigDecimal bedrag();
        String versie();
    }

    interface VergoedingArbeidsongevalBeroepsziekte {
        String reden();
        BigDecimal graadArbeidsOngeschikdheid();
        BigDecimal bedrag();
        String versie();
    }

    interface Vergoeding {
        VergoedingBasis basis();
        List<VergoedingBijdrage> bijdragen();
    }

    interface VergoedingBasis {
        String identificatieWerkgever();
        String paritairComite();
        CodeAndDescription activiteit();
        String typeSchuldenaar();
        LocalDate datumEersteVergoeding();
        String typeAkkoord();
        String halftijdseLoopbaanonderbreking();
        String vrijstellingPrestaties();
        String vervangingInOvereenkomstMetCAO();
        String vervanger();
        String regelingBijWerkhervatting();
        BigDecimal aantalOnderdelenSchadevergoeding();
        LocalDate datumBetekeningOpzegging();
        String ondernemingInMoeilijkheden();
        Periode periodeErkenning();
        String versie();
    }

    interface VergoedingBijdrage {
        String werknemerKengetal();
        String type();
        Integer bedragModificatie();
        String volgnummer();
        BigDecimal vergoeding();
        Integer notieKapitalisatie();
        BigDecimal theoretischBedragBijstanduitkering();
        BigDecimal aantalMaanden();
        BigDecimal aantalMaandenDecimaal();
        OnvolledigeMaand onvolledigeMaand();
        String toepassingVanDeDrempel();
        BigDecimal bedrag();
        String versie();
    }

    interface OnvolledigeMaand {
        BigDecimal aantalDagen();
        String reden();
    }

    interface Result {
        List<CodeAndDescription> resultaten();
    }

    interface Anomalie {
        String bloknummer();
        String lijnnummer();
        String nummer();
        String status();
        String versie();
        LocalDate datumCreatie();
        LocalDate datumBehandeling();
    }
}
