package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import java.time.LocalDate;
import java.util.List;

public interface DmfaAttest {
    List<Attest> attesten();

    String volgendAttest();

    List<Result> antwoordInBatch();

    interface Attest {
        Identification identificatie();
        Versie versie();
        AangifteWerkgever aangifteWerkgever();
        List<Anomalie> anomalien();
    }

    interface Identification {
        String nummer();
        String versie();
        CodeAndDescription status();
        LocalDate dateumCreatie();
    }

    interface Versie{
        String vorigeVersie();
        String volgendeVersie();
    }

    interface AangifteWerkgever{
        String kwartaal();
        String rSZNummer();
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
        Name naam();
        Werknemerslijn werknemerslijn();
    }

    interface Werknemerslijn {
        String categorie();
        String kernGetal();
        Periode periode();
        String grensarbeider();
        String activiteitIvmRisico();
        String nummerLokaleEenheid();
        Double specialeBijdrage();
        Pension pensioen();
        Staving staving();
        List<Tewerkstelling> tewerkstellingen();
        List<Bijdrage> bijdragen();
    }

    interface Periode {
        LocalDate begin();
        LocalDate einde();
    }

    interface Pension {
        Double bediende();
        Double arbeider();
    }

    interface Tewerkstelling {
        String volgnummer();
        String internNummer();
        LokaleEenheid lokaleEenheid();
        String activiteit();
        Periode periode();
        String paritairComite();
        Double aantalWerkdagenPerWeek();
        String typeContract();
        GemiddeldAantalUrenPerWeek gemiddeldAantalUrenPerWeek();
        Informatie infomatie();
        List<Prestatie> prestaties();
    }

    interface Informatie {
        Double brutoLoonZiekte();
    }

    interface GemiddeldAantalUrenPerWeek {
        Double referentiePersoon();
        Double werkNemer();
    }

    interface Prestatie {
        String volgnummer();
        String code();
        Integer dagen();
    }

    interface LokaleEenheid {
        String nummer();
        String nisCode();
    }

    interface Bijdrage {
        String code();
        String type();
        Long basisVoorBerekening();
        Long bedrag();
        String versie();
    }

    interface Name {
        String voornaam();
        String achternaam();
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
