package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * A request that pertains to a person, identified by INSZ number.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>insz: the INSZ number of the party about which the information is requested</li>
 * </ul>
 */
@Getter
public abstract class PersonMagdaRequest extends MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private INSZNumber insz;

        @SuppressWarnings("unchecked")
        public SELF insz(INSZNumber insz) {
            this.insz = insz;
            return (SELF) this;
        }

        public SELF insz(String insz) {
            return insz(INSZNumber.of(insz));
        }
    }

    @NotNull
    private final INSZNumber insz;

    protected PersonMagdaRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration) {
        super(registration);
        this.insz = insz;
    }

    @Override
    public INSZNumber getSubject() {
        return insz;
    }
}