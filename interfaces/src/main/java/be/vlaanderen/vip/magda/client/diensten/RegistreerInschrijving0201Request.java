package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class RegistreerInschrijving0201Request extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private TypeInschrijving type;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate start;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate einde;

        @SuppressWarnings("unchecked")
        public SELF type(TypeInschrijving type) {
            this.type = type;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF start(LocalDate start) {
            this.start = start;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF einde(LocalDate einde) {
            this.einde = einde;
            return (SELF) this;
        }

        public RegistreerInschrijving0201Request build() {
            return new RegistreerInschrijving0201Request(
                    getType(),
                    getInsz(),
                    getStart(),
                    getEinde()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private final TypeInschrijving type;
    private final LocalDate start;
    private final LocalDate einde;

    public RegistreerInschrijving0201Request(TypeInschrijving type, String insz, LocalDate start, LocalDate einde) {
        super(insz, insz);
        this.type = type;
        this.start = start;
        this.einde = einde;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerInschrijving", "02.01.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Start", getStart().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", getEinde().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Type", getType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Type", getType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Sleutel", getInsz()); // XXX why insz here?
        request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}