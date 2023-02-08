package be.vlaanderen.vip.magda.config;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class MagdaRegistrationConfigDto {
    private String key; // default
    private String uri; // kb.vlaanderen.be/aiv/burgerloket-wwoom-aip
    @Nullable private String capacity; // 1300

    public static class Builder {
        private final MagdaRegistrationConfigDto inst;

        public Builder() {
            this.inst = new MagdaRegistrationConfigDto();
        }

        public Builder key(String key) {
            inst.key = key;
            return this;
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
        @Deprecated
        public Builder capacity(String capacity) {
            inst.capacity = capacity;
            return this;
        }

        public MagdaRegistrationConfigDto build() {
            Assert.notNull(inst.key, "key cannot be null.");
            Assert.notNull(inst.uri, "uri cannot be null.");

            return inst;
        }
    }

    /**
     * @deprecated Please use the builder to construct an instance.
     */
    @Deprecated(forRemoval = true)
    public MagdaRegistrationConfigDto(String key, String uri, String capacity) {
        this.key = key;
        this.uri = uri;
        this.capacity = capacity;
    }

    private MagdaRegistrationConfigDto() {}

    public static Builder builder() {
        return new Builder();
    }

    @Deprecated
    public void setKey(String key) {
        this.key = key;
    }

    @Deprecated
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Deprecated
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    /**
     * This field specifies the 'hoedanigheid' code.
     * The use of hoedanigheid codes is being phased out on the side of MAGDA.
     * It is supported here for compatibility, but it's recommended not to use it if possible.
     */
    @Deprecated
    public String getCapacity() {
        return capacity;
    }
}
