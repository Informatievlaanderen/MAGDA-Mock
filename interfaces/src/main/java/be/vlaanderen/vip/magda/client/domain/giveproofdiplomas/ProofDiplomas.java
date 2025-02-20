package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.annotation.Nullable;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Information on a citizen's diploma proofs.
 */
public interface ProofDiplomas {

    static Optional<ProofDiplomas> ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return MagdaResponseProofDiplomasAdapterJaxbImpl.getInstance().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    List<Bewijs> bewijzen();

    interface Bewijs {

        BewijsrefertesLed bewijsrefertesled();

        Naam leverancier();

        Afgeleid afgeleid();

        Basis basis();
    }

    interface BewijsrefertesLed {

        String bewijsreferte();
    }

    interface Afgeleid {

        CodeEnOptioneleNaam iscedNiveau();

        CodeEnNaam iscedStudiegebied();

        CodeEnNaam vksNiveauOnderwijskwalificatie();

        CodeEnNaam vksNiveauBeroepskwalificatie();
    }

    interface Basis {

        Naam authenticiteit();

        Naam bewijsstaat();

        Naam bewijstype();

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

        Uitreikingsdatum uitreikingsdatum();
    }

    interface BijkomendeInformatie {

        String naam();

        String inhoud();
    }

    interface Instelling {

        String naam();

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