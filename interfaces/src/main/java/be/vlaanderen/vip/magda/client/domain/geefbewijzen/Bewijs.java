package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import jakarta.annotation.Nullable;

import java.time.Month;
import java.time.Year;
import java.util.List;

public interface Bewijs {

    BewijsrefertesLed bewijsrefertesled();

    Naam leverancier();

    Afgeleid afgeleid();

    Basis basis();

    interface BewijsrefertesLed {

        String bewijsreferte();
    }

    interface Afgeleid {

        CodeEnOptioneleNaam iscedNiveau();

        CodeEnNaam iscedStudiegebied();

        CodeEnNaam vksNiveauOnderwijskwalificatie();

        CodeEnNaam vksNiveauBeroepskwalificatie();
    }

    interface AlternatieveInstantie {

        Naam instantierol();

        Naam instantie();
    }

    interface Basis {

        @Nullable
        List<AlternatieveInstantie> alternatieveInstanties();

        Naam authenticiteit();

        Naam bewijsstaat();

        Naam bewijstype();

        @Nullable
        List<BijkomendeInformatie> bijkomendeInformaties();

        Naam categorie();

        @Nullable
        NaamEnOptioneleCode detailOnderwerp();

        Naam graad();

        Naam instantie();

        Instelling instelling();

        Code land();

        NaamEnOptioneleCode onderwerp();

        Naam onderwijsvorm();

        Naam schooltype();

        @Nullable
        NaamEnOptioneleCode specialisatie();

        @Nullable
        NaamEnOptioneleCode studierichting();

        Code taal();

        @Nullable
        String vervalperiode();

        String volledigeNaam();

        Uitreikingsdatum uitreikingsdatum();

        @Nullable
        Integer urenVolwassenenonderwijs();
    }

    interface BijkomendeInformatie {

        String naam();

        String inhoud();
    }

    interface Instelling {

        String naam();

        @Nullable
        String nummer();
    }

    interface Uitreikingsdatum {

        Year jaar();

        @Nullable
        Month maand();

        @Nullable
        Integer dag();
    }

    interface Code {

        String code();
    }

    interface Naam {

        String naam();
    }

    interface CodeEnNaam {

        String code();

        String naam();
    }

    interface CodeEnOptioneleNaam {

        String code();

        @Nullable
        String naam();
    }

    interface NaamEnOptioneleCode {

        @Nullable
        String code();

        String naam();
    }
}
