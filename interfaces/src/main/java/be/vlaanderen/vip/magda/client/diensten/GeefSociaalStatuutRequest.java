package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "GeefSociaalStatuut" MAGDA service, which provides information on a given social status for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>socialStatusName: the name of the type of social status about which the information is requested</li>
 * <li>date: the date on which the information on the social status was in effect</li>
 * <li>locationName: the name of the location where the social status was in effect (optional)</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefSociaalStatuut/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1243022119/SocZek.GeefSociaalStatuut-03.00">More information on this request type</a>
 */
@Getter
public class GeefSociaalStatuutRequest extends PersonMagdaRequest {
    
    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        private String socialStatusName;
        private OffsetDateTime date;
        private String locationName;

        public Builder socialStatusName(String socialStatusName) {
            this.socialStatusName = socialStatusName;
            return this;
        }

        public Builder date(OffsetDateTime date) {
            this.date = date;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        /**
         * @deprecated method has been renamed to 'socialStatusName'
         */
        @Deprecated
        public Builder sociaalStatuut(String socialStatusName) {
            return socialStatusName(socialStatusName);
        }

        /**
         * @deprecated method has been renamed to 'date'
         */
        @Deprecated
        public Builder datum(OffsetDateTime date) {
            return date(date);
        }
        
        public GeefSociaalStatuutRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(socialStatusName == null) { throw new IllegalStateException("socialStatusName must be given"); }
            if(date == null) { throw new IllegalStateException("datum must be given"); }

            return new GeefSociaalStatuutRequest(
                    getInsz(),
                    getRegistration(),
                    socialStatusName,
                    date,
                    locationName
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    
    private final String socialStatusName;
    private final OffsetDateTime date;
    private final String locationName;

    protected GeefSociaalStatuutRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull String socialStatusName,
            @NotNull OffsetDateTime date,
            @Nullable String locationName) {
        super(insz, registration);
        this.socialStatusName = socialStatusName;
        this.date = date;
        this.locationName = locationName;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefSociaalStatuut", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        
        request.setValue("//SociaalStatuut/Naam", getSocialStatusName());
        request.setValue("//SociaalStatuut/Datum/Datum", DateTimeFormatter.ISO_LOCAL_DATE.format(getDate()));
        request.removeNode("//SociaalStatuut/Locatie");
        if(getLocationName() != null) {
            request.createNode("//SociaalStatuut", "Locatie");
            request.createTextNode("//SociaalStatuut/Locatie", "Naam", getLocationName());
        }
    }
}
