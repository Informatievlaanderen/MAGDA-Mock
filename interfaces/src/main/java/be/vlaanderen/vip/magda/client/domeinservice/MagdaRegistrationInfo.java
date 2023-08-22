package be.vlaanderen.vip.magda.client.domeinservice;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

/**
 * Information on in name of which party a MAGDA request is to be sent.
 * This comprises an identification string (an arbitrary string value that's determined in agreement with the MAGDA service host),
 * and a so-called "Hoedanigheidscode", which is a legally established code that signifies the type of party.
 *
 * Registration info may be fetched from some {@link MagdaHoedanigheidService} based on a registration code.
 *
 * Note that "hoedanigheidscodes" are presently undergoing a process of deprecation in the MAGDA service.
 * MAGDA Mock supports the use of these codes, but as far as it is concerned, they are optional. Whether they should be used or not depends on the MAGDA service.
 *
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MA/pages/391119396/Hoedanigheidscodes">Documentation on hoedanigheidscodes</a> (link to internal wiki)
 */
@Getter
@ToString
public class MagdaRegistrationInfo {
    private String identification;
    @Nullable private String hoedanigheidscode;

    public static class Builder {
        private final MagdaRegistrationInfo inst;

        public Builder() {
            this.inst = new MagdaRegistrationInfo();
        }

        public Builder identification(String identification) {
            inst.identification = identification;
            return this;
        }

        /**
         * This is the 'hoedanigheidscode'.
         * The use of this field is being phased out on the side of MAGDA.
         * It is supported here for compatibility, but it's recommended not to use it if possible.
         */
        public Builder hoedanigheidscode(String hoedanigheidscode) {
            inst.hoedanigheidscode = hoedanigheidscode;
            return this;
        }

        public MagdaRegistrationInfo build() {
            if(inst.identification == null) {
                throw new IllegalArgumentException("identification cannot be null.");
            }
            // hoedanigheidscode is optional

            return inst;
        }
    }

    private MagdaRegistrationInfo() {}

    public static Builder builder() {
        return new Builder();
    }

    /**
     * This is the 'hoedanigheidcode'.
     * The use of this field is being phased out on the side of MAGDA.
     * It is supported here for compatibility, but it's recommended not to use it if possible.
     */
    public Optional<String> getHoedanigheidscode() {
        return Optional.ofNullable(hoedanigheidscode);
    }
}