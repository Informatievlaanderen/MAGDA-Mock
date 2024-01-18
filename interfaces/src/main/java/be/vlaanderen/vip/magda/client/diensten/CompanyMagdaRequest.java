package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A request that pertains to a company, identified by KBO number.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>kbo: the KBO number of the party about which the information is requested</li>
 * </ul>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class CompanyMagdaRequest extends MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private KBONumber kboNumber;

        @SuppressWarnings("unchecked")
        public SELF kboNumber(KBONumber kboNumber) {
            this.kboNumber = kboNumber;
            return (SELF) this;
        }

        public SELF kboNumber(String kboNumber) {
            return kboNumber(KBONumber.of(kboNumber));
        }
    }

    @NotNull
    private final KBONumber kboNumber;

    protected CompanyMagdaRequest(
            @NotNull KBONumber kboNumber,
            @NotNull String registration) {
        super(registration);
        this.kboNumber = kboNumber;
    }

    @Override
    public KBONumber getSubject() {
        return kboNumber;
    }
}