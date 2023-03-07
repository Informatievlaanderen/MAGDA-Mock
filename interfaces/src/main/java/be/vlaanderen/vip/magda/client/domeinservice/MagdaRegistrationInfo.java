package be.vlaanderen.vip.magda.client.domeinservice;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

@Getter
@ToString
public class MagdaRegistrationInfo {
    private String name;
    private String identification;
    @Nullable private String hoedanigheidscode;

    public static class Builder {
        private final MagdaRegistrationInfo inst;

        public Builder() {
            this.inst = new MagdaRegistrationInfo();
        }

        public Builder name(String name) {
            inst.name = name;
            return this;
        }

        public Builder identification(String identification) {
            inst.identification = identification;
            return this;
        }

        /**
         * This is the 'hoedanigheid' code.
         * The use of this field is being phased out on the side of MAGDA.
         * It is supported here for compatibility, but it's recommended not to use it if possible.
         */
        public Builder hoedanigheidscode(String hoedanigheidscode) {
            inst.hoedanigheidscode = hoedanigheidscode;
            return this;
        }

        public MagdaRegistrationInfo build() {
            Assert.notNull(inst.name, "name cannot be null.");
            Assert.notNull(inst.identification, "identification cannot be null.");
            // hoedanigheidscode is optional

            return inst;
        }
    }

    private MagdaRegistrationInfo() {}

    public static Builder builder() {
        return new Builder();
    }

    /**
     * This is the 'hoedanigheid' code.
     * The use of this field is being phased out on the side of MAGDA.
     * It is supported here for compatibility, but it's recommended not to use it if possible.
     */
    public String getHoedanigheidscode() {
        return hoedanigheidscode;
    }
}