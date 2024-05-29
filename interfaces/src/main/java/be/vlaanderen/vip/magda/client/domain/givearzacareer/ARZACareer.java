package be.vlaanderen.vip.magda.client.domain.givearzacareer;

import java.time.OffsetDateTime;
import java.util.List;

public interface ARZACareer {

    String enterpriseNumber();

    List<Affiliation> affiliations();

    interface Affiliation {

        String socialInsuranceFundEnterpriseNumber();

        List<Career> careers();
    }

    interface Career {

        CodeAndDescription contributionSeries();

        Period period();

        CodeAndDescription periodEquated();

        CodeAndDescription profession();

        CodeAndDescription capacity();

        OffsetDateTime dateOfSignature();
    }

    interface CodeAndDescription {

        String codeValue();

        String codeDescription();

        String descriptionValue();

        String descriptionOrigin();

        String descriptionLanguageCode();
    }

    interface Period {

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }
}