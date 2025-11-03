package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A request to a "mobility/registrations" MAGDA REST service allows you to consult the registration details of a vehicle.
 * <p>
 * The following query parameters exist, all of which are optional
 * <ul>
 * <li>plateNr: Query parameter om de nummerplaat van het voertuig mee te kunnen geven. De nummerplaat dient zonder spaties of leestekens te worden meegegeven.</li>
 * <li>plateUID: Query parameter om de unieke identificatie van het License object gelinkt aan een nummerplaat mee te kunnen geven. Deze identifier wordt als resultaat gegeven in bv de movementindications diensten.</li>
 * <li>vin: Query parameter om het chassisnummer van het voertuig mee te kunnen geven.</li>
 * <li>unifier: Uniek identificatienummer van de nummerplaat. Dit veld mag enkel in combinatie met de parameter "vin" gebruikt worden.</li>
 * <li>certificateId: Query parameter om het unieke identificatienummer  van het certificaat mee te kunnen geven.</li>
 * <li>nationalNr: Query parameter om het rijksregisternummer van een persoon mee te kunnen geven.</li>
 * <li>companyNr: Query parameter om het ondernemingsnummer van een bedrijf mee te kunnen geven.</li>
 * <li>dateTime: Query parameter om de datum waarvoor u de situatie wilt kennen mee te geven. ISO-8601</li>
 * <li>transactionUID: Query parameter om het uniek identificatienummer van een transactie mee te kunnen geven.</li>
 * <li>pageSize: Query parameter om aan te geven hoeveel resultaten per pagina u wilt krijgen.</li>
 * <li>after: Query parameter om aan te geven na welk element de resultaten in het antwoord opgenomen dienen te worden.</li>
 * <li>addressEnrichmentPerson: Query parameter om aan te geven bij welke Bron de recente adresgegevens van de titularis, in geval van Natuurlijk persoon, opgehaald dienen te worden. Mogelijke waardes zijn "RR" (rijksregister) en "KSZ". Indien geen verrijking noodzakelijk kan de parameter weggelaten worden</li>
 * <li>addressEnrichmentOrganization: Query parameter om aan te geven bij of adres verrijking voor onderneming dient plaats te vinden. (Boolean)</li>
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
    private OffsetDateTime dateTime;
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
            if (this.unifier != null && !this.unifier.trim().isEmpty() && (this.vin != null || this.vin.trim().isEmpty())) {
                throw new IllegalArgumentException("Unifier cannot be given without vin");
            }

            return this.internalBuild();
        }
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
