package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * A request to a "GeefSociaalStatuut" MAGDA service, which provides information on one or multiple social statutes for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>socialStatutes: The search criteria for social statutes. Up to 8 social statutes can be specified.</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefSociaalStatuut/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1243022119/SocZek.GeefSociaalStatuut-03.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefMultipleSociaalStatuutRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        @Getter(AccessLevel.PROTECTED)
        private Set<SociaalStatuutRequestCriteria> socialStatutes;

        public Builder socialStatutes(Set<SociaalStatuutRequestCriteria> socialStatutes) {
            this.socialStatutes = socialStatutes;
            return this;
        }

        public GeefMultipleSociaalStatuutRequest build() {
            if (getInsz() == null) {
                throw new IllegalStateException("INSZ number must be given");
            }
            if (socialStatutes == null) {
                throw new IllegalStateException("socialStatutes must be given");
            }
            if (socialStatutes.isEmpty()) {
                throw new IllegalStateException("socialStatutes must contain at least 1 entry");
            }
            if (socialStatutes.size() > 8) {
                throw new IllegalStateException("socialStatutes cannot contain more than 8 entries");
            }

            return new GeefMultipleSociaalStatuutRequest(
                    getInsz(),
                    getRegistration(),
                    socialStatutes
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final Set<SociaalStatuutRequestCriteria> socialStatutes;

    public GeefMultipleSociaalStatuutRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull Set<SociaalStatuutRequestCriteria> socialStatutes) {
        super(insz, registration);
        this.socialStatutes = socialStatutes;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefSociaalStatuut", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        //Remove dummy from template
        request.removeNode("//SociaalStatuut");

        socialStatutes.forEach(x -> {
            writeSocialStatuteCriteria(request, x);
        });
    }

    private void writeSocialStatuteCriteria(MagdaDocument request, SociaalStatuutRequestCriteria socialStatute) {
        /*
            <SociaalStatuut>
                <Naam>Naam</Naam>
                <Datum>
                    <Datum>Datum</Datum>
                    <Periode>
                        <Begindatum>Begindatum</Begindatum>
                        <Einddatum>Einddatum</Einddatum>
                    </Periode>
                </Datum>
                <Locatie>
                    <Naam>Locatie</Naam>
                </Locatie>
            </SociaalStatuut>
         */
        var doc = request.getXml();
        var sociaalStatuutNode = doc.createElement("SociaalStatuut");
        //Include name
        var nameNode = doc.createElement("Naam");
        nameNode.appendChild(doc.createTextNode(socialStatute.getSocialStatusName()));
        sociaalStatuutNode.appendChild(nameNode);
        //Include date or period
        var dateNode = doc.createElement("Datum");
        if (socialStatute.getDate() != null) {
            var dateValueNode = doc.createElement("Datum");
            dateValueNode.appendChild(doc.createTextNode(DateTimeFormatter.ISO_LOCAL_DATE.format(socialStatute.getDate())));
            dateNode.appendChild(dateValueNode);
        }
        if (socialStatute.getStartDate() != null) {
            var periodNode = doc.createElement("Periode");
            var startDateNode = doc.createElement("Begindatum");
            startDateNode.appendChild(doc.createTextNode(DateTimeFormatter.ISO_LOCAL_DATE.format(socialStatute.getStartDate())));
            periodNode.appendChild(startDateNode);
            if (socialStatute.getEndDate() != null) {
                var endDateNode = doc.createElement("Einddatum");
                endDateNode.appendChild(doc.createTextNode(DateTimeFormatter.ISO_LOCAL_DATE.format(socialStatute.getEndDate())));
                periodNode.appendChild(endDateNode);
            }
            dateNode.appendChild(periodNode);
        }
        sociaalStatuutNode.appendChild(dateNode);
        //Include location
        if (socialStatute.getLocationName() != null) {
            var locationNode = doc.createElement("Locatie");
            var locationNameNode = doc.createElement("Naam");
            locationNameNode.appendChild(doc.createTextNode(socialStatute.getLocationName()));
            locationNode.appendChild(locationNameNode);
            sociaalStatuutNode.appendChild(locationNode);
        }

        request.xpath("//SocialeStatuten").item(0).appendChild(sociaalStatuutNode);
    }
}

