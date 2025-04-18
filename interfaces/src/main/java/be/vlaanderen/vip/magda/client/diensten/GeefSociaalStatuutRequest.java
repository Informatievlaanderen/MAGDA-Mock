package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * A request to a "GeefSociaalStatuut" MAGDA service, which provides information on a given social status for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>socialStatusName: the name of the type of social status about which the information is requested</li>
 * <li>atDateOfRequest: if true, the date used in the criteria is to be the same as the date used in the Tijdstip/Datum field from the request header. Mutually exclusive with other date parameters.</li>
 * <li>date: the date on which the information on the social status was in effect.</li>
 * <li>startDate: required, the start date of the period in which the social status was in effect</li>
 * <li>endDate: optional, the end date of the period in which the social status was in effect. When not specified, the period is assumed to run until today.</li>
 * <li>locationName: the name of the location where the social status was in effect (optional)</li>
 * </ul>
 * The "GeefSociaalStatuut" MAGDA service can be queried by a specific date or by period using "startDate" and/or "endDate".
 *
 * @see <a href="file:resources/templates/GeefSociaalStatuut/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1243022119/SocZek.GeefSociaalStatuut-03.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefSociaalStatuutRequest extends PersonMagdaRequest {
    
    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        @Getter(AccessLevel.PROTECTED)
        private String socialStatusName;
        @Getter(AccessLevel.PROTECTED)
        private boolean atDateOfRequest;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate date;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;
        @Getter(AccessLevel.PROTECTED)
        private String locationName;

        public Builder socialStatusName(String socialStatusName) {
            this.socialStatusName = socialStatusName;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder atDateOfRequest(boolean atDateOfRequest) {
            this.atDateOfRequest = atDateOfRequest;
            return this;
        }

        public Builder startDate(LocalDate date) {
            this.startDate = date;
            return this;
        }

        public Builder endDate(LocalDate date) {
            this.endDate = date;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        /**
         * @deprecated data type OffsetDateTime has been replaced by LocalDate.
         */
        @Deprecated(forRemoval=true)
        public Builder date(OffsetDateTime date) {
            return date(date.toLocalDate());
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
            if(atDateOfRequest) {
                if((date != null || startDate != null)) { throw new IllegalStateException("When atDateOfRequest is to be used, no date or startDate may be specified"); }
            } else if((date == null && startDate == null) || (date != null && startDate != null)) {
                throw new IllegalStateException("Either date or startDate must be given");
            }
            if(startDate == null && endDate != null) { throw new IllegalStateException("endDate cannot be given without startDate"); }

            return new GeefSociaalStatuutRequest(
                    getInsz(),
                    getRegistration(),
                    socialStatusName,
                    atDateOfRequest,
                    date,
                    startDate,
                    endDate,
                    locationName
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final String socialStatusName;
    private final boolean atDateOfRequest;
    @Nullable
    private final LocalDate date;
    @Nullable
    private final LocalDate startDate;
    @Nullable
    private final LocalDate endDate;
    @Nullable
    private final String locationName;

    protected GeefSociaalStatuutRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registration,
            @NotNull String socialStatusName,
            boolean atDateOfRequest,
            @Nullable LocalDate date,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String locationName) {
        super(insz, registration);
        this.socialStatusName = socialStatusName;
        this.atDateOfRequest = atDateOfRequest;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationName = locationName;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefSociaalStatuut", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo, instant);
        
        request.setValue("//SociaalStatuut/Naam", getSocialStatusName());

        if(isAtDateOfRequest()) {
            request.setValue("//SociaalStatuut/Datum/Datum", request.getValue("//Context/Bericht/Tijdstip/Datum"));
        } else {
            if(getDate() != null) {
                request.setValue("//SociaalStatuut/Datum/Datum", DateTimeFormatter.ISO_LOCAL_DATE.format(getDate()));
            } else {
                request.removeNode("//SociaalStatuut/Datum/Datum");
            }

            if(getStartDate() != null) {
                request.createNode("//SociaalStatuut/Datum", "Periode");
                request.createNode("//SociaalStatuut/Datum/Periode", "Begindatum");
                request.setValue("//SociaalStatuut/Datum/Periode/Begindatum", DateTimeFormatter.ISO_LOCAL_DATE.format(getStartDate()));
                if(getEndDate() != null) {
                    request.createNode("//SociaalStatuut/Datum/Periode", "Einddatum");
                    request.setValue("//SociaalStatuut/Datum/Periode/Einddatum", DateTimeFormatter.ISO_LOCAL_DATE.format(getEndDate()));
                }
            }
        }

        if(getLocationName() != null) {
            request.setValue("//SociaalStatuut/Locatie/Naam", getLocationName());
        } else {
            request.removeNode("//SociaalStatuut/Locatie");
        }
    }
}
