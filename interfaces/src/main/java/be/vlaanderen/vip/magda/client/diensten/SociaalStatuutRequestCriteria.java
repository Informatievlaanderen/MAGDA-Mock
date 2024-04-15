package be.vlaanderen.vip.magda.client.diensten;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.time.LocalDate;

/**
 * The search criteria for "GeefSociaalStatuut" MAGDA service.
 * <ul>
 * <li>socialStatusName: the name of the type of social status about which the information is requested</li>
 * <li>date: the date on which the information on the social status was in effect.</li>
 * <li>startDate: required, the start date of the period in which the social status was in effect</li>
 * <li>endDate: optional, the end date of the period in which the social status was in effect. When not specified, the period is assumed to run until today.</li>
 * <li>locationName: the name of the location where the social status was in effect (optional)</li>
 * </ul>
 * Either a date or a period using "startDate" and/or "endDate" has to be specified.
 */
@Getter
@ToString
@EqualsAndHashCode
public class SociaalStatuutRequestCriteria
{
    public static class Builder {
        @Getter(AccessLevel.PROTECTED)
        private String socialStatusName;
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

        public SociaalStatuutRequestCriteria build() {
            if(socialStatusName == null) { throw new IllegalStateException("socialStatusName must be given"); }
            if((date == null && startDate == null) || (date != null && startDate != null)) { throw new IllegalStateException("Either date or startDate must be given"); }
            if(startDate == null && endDate != null) { throw new IllegalStateException("endDate cannot be given without startDate"); }

            return new SociaalStatuutRequestCriteria(
                    socialStatusName,
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
    @Nullable
    private final LocalDate date;
    @Nullable
    private final LocalDate startDate;
    @Nullable
    private final LocalDate endDate;
    @Nullable
    private final String locationName;

    protected SociaalStatuutRequestCriteria(
            @NotNull String socialStatusName,
            @Nullable LocalDate date,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String locationName) {
        this.socialStatusName = socialStatusName;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationName = locationName;
    }
}
