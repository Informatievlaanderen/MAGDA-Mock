package be.vlaanderen.vip.magda.config;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class MagdaRegistrationConfigDto {

    private String identification;
    @Nullable private String hoedanigheidscode;

    public static class Builder {

        private final MagdaRegistrationConfigDto inst;

        public Builder() {
            this.inst = new MagdaRegistrationConfigDto();
        }

        public Builder identification(String identification) {
            inst.identification = identification;
            return this;
        }

        /**
         * This field specifies the 'hoedanigheid' code.
         * The use of hoedanigheid codes is being phased out on the side of MAGDA.
         * It is supported here for compatibility, but it's recommended not to use it if possible.
         */
        public Builder hoedanigheidscode(String hoedanigheidscode) {
            inst.hoedanigheidscode = hoedanigheidscode;
            return this;
        }

        public MagdaRegistrationConfigDto build() {
            Assert.notNull(inst.identification, "identification cannot be null.");
            // hoedanigheidscode is optional

            return inst;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private MagdaRegistrationConfigDto() {}

    /**
     * This field specifies the 'hoedanigheid' code.
     * The use of hoedanigheid codes is being phased out on the side of MAGDA.
     * It is supported here for compatibility, but it's recommended not to use it if possible.
     */
    public String getHoedanigheidscode() {
        return hoedanigheidscode;
    }
}
