package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.MalformedMagdaResponseException;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Information on a citizen's social statute
 */
public interface SocialStatute {

    /**
     * A numeric identifier that signifies the social statute.
     */
    default Integer socialStatuteCode() {
        var result = result();
        if(result != null) {
            var code = result.code();
            if(code != null) {
                return Integer.parseInt(code);
            } else {
                throw new MalformedMagdaResponseException("Magda response document misses an expected social statute result code node");
            }
        } else {
            throw new MalformedMagdaResponseException("Magda response document misses an expected social statute result node");
        }
    }

    String name();

    Result result();

    List<Period> periods();

    Status status();

    interface Result {

        String code();
    }

    interface Period {

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Status {

        String code();
    }
    
}
