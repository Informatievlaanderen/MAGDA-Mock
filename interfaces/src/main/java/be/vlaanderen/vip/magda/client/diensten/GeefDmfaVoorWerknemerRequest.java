package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domain.dto.Kwartaal;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Een request voor de "GeefDmfaVoorWerknemer" MAGDA Service, deze biedt DmfA informatie voor een gegeven INSZ nummer.
 * Bovenop de {@link PersonMagdaRequest} zijn er volgende velden:
 * <ul>
 *   <li>beginQuarter: begin van de periode waarop de vraag betrekking heeft (jaar en kwartaal)</li>
 *   <li>endQuarter: einde van de periode waarop de vraag betrekking heeft (jaar en kwartaal)</li>
 *   <li>typeAntwoord: online (onmiddelijk) antwoord of semi-online</li>
 *   <li>laatsteSituatie: alle situaties of enkel de huidige situatie</li>
 *   <li>bron: attesten bij RSZ of DIBISS (lokale sociale zekerheid) ophalen</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefDmfaVoorWerknemer/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1355220729/Werk.GeefDmfaVoorWerknemer-03.00">Meer info over deze request</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefDmfaVoorWerknemerRequest extends PersonMagdaRequest {

    @NotNull
    private final Kwartaal beginKwartaal;
    @NotNull
    private final Kwartaal eindeKwartaal;
    @Nullable
    private final TypeAntwoord typeAntwoord;
    @Nullable
    private final LaatsteSituatie laatsteSituatie;
    @Nullable
    private final Bron bron;

    @Nullable
    private GeefDmfaVoorWerknemerRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registratie,
            @NotNull Kwartaal beginKwartaal,
            @NotNull Kwartaal eindeKwartaal,
            @Nullable TypeAntwoord typeAntwoord,
            @Nullable LaatsteSituatie laatsteSituatie,
            @Nullable Bron bron) {
        super(insz, registratie);
        this.beginKwartaal = beginKwartaal;
        this.eindeKwartaal = eindeKwartaal;
        this.typeAntwoord = typeAntwoord;
        this.laatsteSituatie = laatsteSituatie;
        this.bron = bron;
    }

    public static GeefDmfaVoorWerknemerRequest.Builder builder() {
        return new GeefDmfaVoorWerknemerRequest.Builder();
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefDmfaVoorWerknemer", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo, instant);

        request.setValue("//Criteria/Kwartaal/Begin/Jaar", String.valueOf(beginKwartaal.getJaar()));
        request.setValue("//Criteria/Kwartaal/Begin/Kwartaalcijfer", String.valueOf(beginKwartaal.getKwartaalcijfer()));
        request.setValue("//Criteria/Kwartaal/Einde/Jaar", String.valueOf(eindeKwartaal.getJaar()));
        request.setValue("//Criteria/Kwartaal/Einde/Kwartaalcijfer", String.valueOf(eindeKwartaal.getKwartaalcijfer()));

        if (typeAntwoord != null) {
            request.setValue("//TypeAntwoord", typeAntwoord.getTypeString());
        } else {
            request.removeNode("//TypeAntwoord");
        }
        if (laatsteSituatie != null) {
            request.setValue("//LaatsteSituatie", laatsteSituatie.getTypeString());
        } else {
            request.removeNode("//LaatsteSituatie");
        }
        if (bron != null) {
            request.setValue("//Bron", bron.getTypeString());
        } else {
            request.removeNode("//Bron");
        }
    }

    @Getter
    public enum TypeAntwoord {
        ONLINE("0"),
        SEMI_ONLINE("1");

        private final String typeString;

        TypeAntwoord(String typeString) {
            this.typeString = typeString;
        }
    }


    @Getter
    public enum LaatsteSituatie {
        ALLE_SITUATIES("0"),
        HUIDIGE_SITUATIE("1");

        private final String typeString;

        LaatsteSituatie(String typeString) {
            this.typeString = typeString;
        }
    }


    @Getter
    public enum Bron {
        RSZ("RSZ"),
        DIBISS("DIBISS"),
        ONBEKEND("Niet opgegeven");

        private final String typeString;

        Bron(String typeString) {
            this.typeString = typeString;
        }
    }

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private PersonSource source;
        @Getter(AccessLevel.PROTECTED)
        private Kwartaal beginKwartaal;
        @Getter(AccessLevel.PROTECTED)
        private Kwartaal endKwartaal;
        @Getter(AccessLevel.PROTECTED)
        private TypeAntwoord typeAntwoord;
        @Getter(AccessLevel.PROTECTED)
        private LaatsteSituatie laatsteSituatie;
        @Getter(AccessLevel.PROTECTED)
        private Bron bron;

        public GeefDmfaVoorWerknemerRequest.Builder source(PersonSource source) {
            this.source = source;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest.Builder beginKwartaal(Kwartaal beginKwartaal) {
            this.beginKwartaal = beginKwartaal;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest.Builder eindeKwartaal(Kwartaal endKwartaal) {
            this.endKwartaal = endKwartaal;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest.Builder typeAntwoord(TypeAntwoord typeAntwoord) {
            this.typeAntwoord = typeAntwoord;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest.Builder laatsteSituatie(LaatsteSituatie laatsteSituatie) {
            this.laatsteSituatie = laatsteSituatie;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest.Builder bron(Bron bron) {
            this.bron = bron;
            return this;
        }

        public GeefDmfaVoorWerknemerRequest build() {
            if (getInsz() == null) {
                throw new IllegalStateException("INSZ number must be given");
            }
            if (beginKwartaal == null || endKwartaal == null) {
                throw new IllegalStateException("Begin en eindkwartaal moeten gegeven zijn");
            }

            return new GeefDmfaVoorWerknemerRequest(
                    getInsz(),
                    getRegistration(),
                    getBeginKwartaal(),
                    getEndKwartaal(),
                    getTypeAntwoord(),
                    getLaatsteSituatie(),
                    getBron()
            );
        }
    }
}
