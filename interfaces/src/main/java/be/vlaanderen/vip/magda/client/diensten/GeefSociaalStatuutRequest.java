package be.vlaanderen.vip.magda.client.diensten;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GeefSociaalStatuutRequest extends PersonMagdaRequest {
    
    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        private String sociaalStatuut;
        private OffsetDateTime datum;
        
        public Builder sociaalStatuut(String sociaalStatuut) {
            this.sociaalStatuut = sociaalStatuut;
            return this;
        }
        
        public Builder datum(OffsetDateTime datum) {
            this.datum = datum;
            return this;
        }
        
        public GeefSociaalStatuutRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(sociaalStatuut == null) { throw new IllegalStateException("SociaalStatuut must be given"); }
            if(datum == null) { throw new IllegalStateException("datum must be given"); }

            return new GeefSociaalStatuutRequest(
                    getInsz(),
                    getRegistration(),
                    sociaalStatuut,
                    datum
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    
    private String sociaalStatuut;
    private OffsetDateTime datum;

    protected GeefSociaalStatuutRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull String sociaalStatuut,
            OffsetDateTime datum) {
        super(insz, registration);
        this.sociaalStatuut = sociaalStatuut;
        this.datum = datum;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefSociaalStatuut", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        
        request.setValue("//SociaalStatuut/Naam", sociaalStatuut);
        request.setValue("//SociaalStatuut/Datum/Datum", DateTimeFormatter.ISO_LOCAL_DATE.format(datum));
    }

}
