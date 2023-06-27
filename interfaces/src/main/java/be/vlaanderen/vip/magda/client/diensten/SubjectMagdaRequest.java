package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class SubjectMagdaRequest extends MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private SubjectIdentificationNumber subject;

        @SuppressWarnings("unchecked")
        public SELF subject(SubjectIdentificationNumber subject) {
            this.subject = subject;
            return (SELF) this;
        }

        public SELF insz(String insz) {
            return subject(INSZNumber.of(insz));
        }

        public SELF kbo(String kbo) {
            return subject(KBONumber.of(kbo));
        }
    }

    @NotNull
    private final SubjectIdentificationNumber subject;

    protected SubjectMagdaRequest(
            @NotNull SubjectIdentificationNumber subject,
            @NotNull String registration) {
        super(registration);
        this.subject = subject;
    }

    @Override
    public SubjectIdentificationNumber getSubject() {
        return subject;
    }
}