package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @see <a href="file:resources/templates/GeefHistoriekPersoon/02.02.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1121977193/Persoon.GeefHistoriekPersoon-02.02">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefHistoriekPersoonRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<GeefHistoriekPersoonRequest.Builder> {

        @Getter(AccessLevel.PROTECTED)
        private PersonSource bron;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate vanDatum;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate opDatum;

        public GeefHistoriekPersoonRequest.Builder vanDatum(LocalDate vanDatum) {
            this.vanDatum = vanDatum;
            return this;
        }

        public GeefHistoriekPersoonRequest.Builder opDatum(LocalDate opDatum) {
            this.opDatum = opDatum;
            return this;
        }

        public GeefHistoriekPersoonRequest.Builder bron(PersonSource bron) {
            this.bron = bron;
            return this;
        }

        public GeefHistoriekPersoonRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ moet aanwezig zijn"); }
            if(getBron() == null) { throw new IllegalStateException("Bron moet aanwezig zijn"); }
            if(getVanDatum() == null && getOpDatum() == null) { throw new IllegalStateException("VanDatum of OpDatum moet aanwezig zijn"); }
            if(getVanDatum() != null && getOpDatum() != null) { throw new IllegalStateException("VanDatum of OpDatum kunnen niet beide aanwezig zijn."); }

            return new GeefHistoriekPersoonRequest(
                    getInsz(),
                    getRegistration(),
                    getVanDatum(),
                    getOpDatum(),
                    getBron()
            );
        }
    }

    public static GeefHistoriekPersoonRequest.Builder builder() {
        return new GeefHistoriekPersoonRequest.Builder();
    }

    private final PersonSource bron;
    private final LocalDate vanDatum;
    private final LocalDate opDatum;

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefHistoriekPersoon", "02.02.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        request.setValue("//Inhoud/Bron", bron.getValue());
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vragen/Vraag/Inhoud/Criteria/Datum", getVanDatum() != null ? getVanDatum().format(dateFormatter) : getOpDatum().format(dateFormatter));
        request.removeAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op");
        request.createAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum", "Op", getOpDatum() != null ? "1" : "0");
    }

    private GeefHistoriekPersoonRequest(@NotNull INSZNumber inszNumber,
                                        @NotNull String registration,
                                        @NotNull LocalDate vanDatum,
                                        @Nullable LocalDate opDatum,
                                        @NotNull PersonSource personSource) {
        super(inszNumber, registration);
        this.vanDatum = vanDatum;
        this.opDatum = opDatum;
        this.bron = personSource;
    }
}

