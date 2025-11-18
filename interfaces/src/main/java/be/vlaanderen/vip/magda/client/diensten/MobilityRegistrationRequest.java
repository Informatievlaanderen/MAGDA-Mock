package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A request to a "mobility/registrations" MAGDA REST service allows you to consult the registration details of a vehicle.
 * <p>
 * The following query parameters exist, all of which are optional
 * <ul>
 * <li>plateNr: Query parameter for the license plate number, without whitespace or punctuation.</li>
 * <li>plateUID: Query parameter for the unique identifier of the license object linked to a license plate. This identifier can be the result of other services.</li>
 * <li>vin: Query parameter for the VIN (Vehicle Identification Number).</li>
 * <li>unifier: Unique identifier of the license plate. This parameter should only be used in combination with the "vin" paramater.</li>
 * <li>certificateId: Query parameter for the unique identifier of the certificate.</li>
 * <li>nationalNr: Query parameter for the national registration number of a person.</li>
 * <li>companyNr: Query parameter for the company number of a company.</li>
 * <li>dateTime: Query parameter for the date on which the situation is queried in ISO-8601 format.</li>
 * <li>transactionUID: Query parameter for the unique identification number of the transaction.</li>
 * <li>pageSize: Query parameter for paging, for the amount of results per page.</li>
 * <li>after: Query parameter for paging, after which result to return the amount as specified in pageSize.</li>
 * <li>addressEnrichmentPerson: Query parameter to indicate which source to use to get the address details of the Titular, in the case it's a natural person. Possible values: "RR" (national registry) and "KSZ". If there's no need for enrichment with the address this parameter can be omitted.</li>
 * <li>addressEnrichmentOrganization: Query parameter to indicate whether to fetch the address details of the Titular, in the case it's a company.</li>
 * </ul>
 *
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1920893376/Beschrijving+Vraag+GET+mobility+registrations+-+v1">Documentation on this request</a>
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true, buildMethodName = "internalBuild")
public class MobilityRegistrationRequest {

    @Getter(AccessLevel.PROTECTED)
    private String plateNr;
    @Getter(AccessLevel.PROTECTED)
    private String plateUID;
    @Getter(AccessLevel.PROTECTED)
    private String vin;
    @Getter(AccessLevel.PROTECTED)
    private String unifier;
    @Getter(AccessLevel.PROTECTED)
    private String certificateId;
    @Getter(AccessLevel.PROTECTED)
    private String nationalNr;
    @Getter(AccessLevel.PROTECTED)
    private String companyNr;
    @Getter(AccessLevel.PROTECTED)
    private LocalDate dateTime;
    @Getter(AccessLevel.PROTECTED)
    private String transactionUID;
    @Getter(AccessLevel.PROTECTED)
    private String pageSize;
    @Getter(AccessLevel.PROTECTED)
    private String after;
    @Getter(AccessLevel.PROTECTED)
    private EnrichmentSource addressEnrichmentPerson;
    @Getter(AccessLevel.PROTECTED)
    private String addressEnrichmentOrganization;

    @Getter
    private UUID correlationId;
    @Getter
    private MagdaRegistrationInfo registrationInfo;
    @Getter
    private String enduserId;

    @Getter
    public enum EnrichmentSource {
        RR("RR"),
        KSZ("KSZ");

        private final String value;

        EnrichmentSource(String value) {
            this.value = value;
        }
    }

    public static class MobilityRegistrationRequestBuilder {
        public MobilityRegistrationRequest build() {
            if (!isNullOrBlank(this.unifier) && isNullOrBlank(this.vin)) {
                throw new IllegalArgumentException("Unifier cannot be given without vin");
            }
            if (correlationId == null) {
                correlationId = UUID.randomUUID();
            }
            if (registrationInfo == null) {
                throw new IllegalArgumentException("RegistrationInfo must be given");
            }

            return this.internalBuild();
        }
    }

    private static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    public Map<String, String> getQueryParameters() {
        HashMap<String, String> map = new HashMap<>();
        if (this.plateNr != null) {
            map.put("plateNr", this.plateNr);
        }
        if (this.plateUID != null) {
            map.put("plateUID", this.plateUID);
        }
        if (this.vin != null) {
            map.put("vin", this.vin);
        }
        if (this.unifier != null) {
            map.put("unifier", this.unifier);
        }
        if (this.certificateId != null) {
            map.put("certificateId", this.certificateId);
        }
        if (this.nationalNr != null) {
            map.put("nationalNr", this.nationalNr);
        }
        if (this.companyNr != null) {
            map.put("companyNr", this.companyNr);
        }
        if (this.dateTime != null) {
            map.put("dateTime", this.dateTime.toString());
        }
        if (this.transactionUID != null) {
            map.put("transactionUID", this.transactionUID);
        }
        if (this.pageSize != null) {
            map.put("pageSize", this.pageSize);
        }
        if (this.after != null) {
            map.put("after", this.after);
        }
        if (this.addressEnrichmentPerson != null) {
            map.put("addressEnrichmentPerson", this.addressEnrichmentPerson.toString());
        }
        if (this.addressEnrichmentOrganization != null) {
            map.put("addressEnrichmentOrganization", this.addressEnrichmentOrganization);
        }
        return map;
    }
}
