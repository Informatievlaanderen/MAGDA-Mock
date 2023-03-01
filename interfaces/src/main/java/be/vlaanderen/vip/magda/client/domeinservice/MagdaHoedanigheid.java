package be.vlaanderen.vip.magda.client.domeinservice;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

@Getter
@ToString
public class MagdaHoedanigheid {
    private String naam;
    private String uri;
    @Nullable private String hoedanigheid;

    public static class Builder {
        private final MagdaHoedanigheid inst;

        public Builder() {
            this.inst = new MagdaHoedanigheid();
        }

        public Builder naam(String naam) {
            inst.naam = naam;
            return this;
        }

        public Builder uri(String uri) {
            inst.uri = uri;
            return this;
        }

        /**
         * This is the 'hoedanigheid' code.
         * The use of this field is being phased out on the side of MAGDA.
         * It is supported here for compatibility, but it's recommended not to use it if possible.
         */
        @Deprecated
        public Builder hoedanigheid(String hoedanigheid) {
            inst.hoedanigheid = hoedanigheid;
            return this;
        }

        public MagdaHoedanigheid build() {
            Assert.notNull(inst.naam, "naam cannot be null.");
            Assert.notNull(inst.uri, "uri cannot be null.");

            return inst;
        }
    }

    /**
     * @deprecated Please use the builder to construct an instance.
     */
    @Deprecated(forRemoval = true)
    public MagdaHoedanigheid(String naam, String uri, String hoedanigheid) {
        this.naam = naam;
        this.uri = uri;
        this.hoedanigheid = hoedanigheid;
    }

    private MagdaHoedanigheid() {}

    public static Builder builder() {
        return new Builder();
    }

    /**
     * This is the 'hoedanigheid' code.
     * The use of this field is being phased out on the side of MAGDA.
     * It is supported here for compatibility, but it's recommended not to use it if possible.
     */
    @Deprecated
    public String getHoedanigheid() {
        return hoedanigheid;
    }
}