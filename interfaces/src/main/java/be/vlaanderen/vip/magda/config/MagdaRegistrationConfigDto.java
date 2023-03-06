package be.vlaanderen.vip.magda.config;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class MagdaRegistrationConfigDto {

    private String uri;
    @Nullable private String capacity; // XXX rename to "hoedanigheidscode"

    public static class Builder {

        private final MagdaRegistrationConfigDto inst;

        public Builder() {
            this.inst = new MagdaRegistrationConfigDto();
        }

        public Builder uri(String uri) {
            inst.uri = uri;
            return this;
        }

        /**
         * This field specifies the 'hoedanigheid' code.
         * The use of hoedanigheid codes is being phased out on the side of MAGDA.
         * It is supported here for compatibility, but it's recommended not to use it if possible.
         */
        public Builder capacity(String capacity) {
            inst.capacity = capacity;
            return this;
        }

        public MagdaRegistrationConfigDto build() {
            Assert.notNull(inst.uri, "uri cannot be null.");

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
    public String getCapacity() {
        return capacity;
    }
}
