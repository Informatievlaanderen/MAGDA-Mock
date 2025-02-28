package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Bewijs;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;
import java.util.UUID;

/**
 * A request to a "LED.RegistreerBewijs" MAGDA service, which registers diploma proofs for the INSZ number of the citizen to whom a diploma has been awarded.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>leverancierNaam: the name of the supplier</li>
 * <li>leverancierBewijsreferte: the LED refernce of the supplier</li>
 * <li>bewijsBasis: the basic content of the proof to be registered</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/RegistreerBewijs/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1204979049/LED.RegistreerBewijs-02.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RegistreerBewijsRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<RegistreerBewijsRequest.Builder> {

        @Getter(AccessLevel.PROTECTED)
        String leverancierNaam;
        @Getter(AccessLevel.PROTECTED)
        String leverancierBewijsreferte;
        @Getter(AccessLevel.PROTECTED)
        Bewijs.Basis bewijsBasis;

        public Builder leverancierNaam(String leverancierNaam) {
            this.leverancierNaam = leverancierNaam;
            return this;
        }

        public Builder leverancierBewijsreferte(String leverancierBewijsreferte) {
            this.leverancierBewijsreferte = leverancierBewijsreferte;
            return this;
        }

        public Builder bewijsBasis(Bewijs.Basis bewijsBasis) {
            this.bewijsBasis = bewijsBasis;
            return this;
        }

        public RegistreerBewijsRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number (for the PersoonUitgereikt field) must be given"); }
            if(getLeverancierNaam() == null) { throw new IllegalStateException("leverancierNaam must be given"); }
            if(getLeverancierBewijsreferte() == null) { throw new IllegalStateException("leverancierBewijsreferte must be given"); }
            if(getBewijsBasis() == null) { throw new IllegalStateException("bewijsBasis must be given"); }

            return new RegistreerBewijsRequest(
                    getInsz(),
                    getRegistration(),
                    getLeverancierNaam(),
                    getLeverancierBewijsreferte(),
                    getBewijsBasis()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final String leverancierNaam;
    @NotNull
    private final String leverancierBewijsreferte;
    @NotNull
    private final Bewijs.Basis bewijsBasis;

    public RegistreerBewijsRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull String leverancierNaam,
            @NotNull String leverancierBewijsreferte,
            @NotNull Bewijs.Basis bewijsBasis) {
        super(insz, registration);
        this.leverancierNaam = leverancierNaam;
        this.leverancierBewijsreferte = leverancierBewijsreferte;
        this.bewijsBasis = bewijsBasis;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerBewijs", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        request.setValue("//Bewijsregistratie/Leverancier/Naam", leverancierNaam);
        request.setValue("//Bewijsregistratie/Leverancier/Bewijsreferte", leverancierBewijsreferte);
        request.setValue("//Bewijsregistratie/PersoonUitgereikt/INSZ", getInsz().getValue());
        request.setValue("//Bewijsregistratie/Bewijs/Authenticiteit/Naam", bewijsBasis.authenticiteit().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Categorie/Naam", bewijsBasis.categorie().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Graad/Naam", bewijsBasis.graad().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Onderwijsvorm/Naam", bewijsBasis.onderwijsvorm().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Bewijstype/Naam", bewijsBasis.bewijstype().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Bewijsstaat/Naam", bewijsBasis.bewijsstaat().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Instantie/Naam", bewijsBasis.instantie().naam());
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Onderwerp/Code", bewijsBasis.onderwerp().code());
        request.setValue("//Bewijsregistratie/Bewijs/Onderwerp/Naam", bewijsBasis.onderwerp().naam());
        request.setValue("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Jaar", bewijsBasis.uitreikingsdatum().jaar().toString());
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Maand", Optional.ofNullable(bewijsBasis.uitreikingsdatum().maand())
                .map(month -> String.format("%02d", month.getValue()))
                .orElse(null));
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Uitreikingsdatum/Dag", Optional.ofNullable(bewijsBasis.uitreikingsdatum().dag())
                .map(dag -> String.format("%02d", dag))
                .orElse(null));
        request.setValue("//Bewijsregistratie/Bewijs/VolledigeNaam", bewijsBasis.volledigeNaam());
        request.setValue("//Bewijsregistratie/Bewijs/Land/Code", bewijsBasis.land().code());
        request.setValue("//Bewijsregistratie/Bewijs/Taal/Code", bewijsBasis.taal().code());
        request.setValue("//Bewijsregistratie/Bewijs/Instelling/Naam", bewijsBasis.instelling().naam());
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Instelling/Nummer", bewijsBasis.instelling().nummer());
        request.setValue("//Bewijsregistratie/Bewijs/Schooltype/Naam", bewijsBasis.schooltype().naam());
        var studierichting = bewijsBasis.studierichting();
        if(studierichting != null) {
            request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Studierichting/Naam", studierichting.naam());
            request.setValue("//Bewijsregistratie/Bewijs/Studierichting/Code", studierichting.code());
        } else {
            request.removeNode("//Bewijsregistratie/Bewijs/Studierichting");
        }
        var specialisatie = bewijsBasis.specialisatie();
        if(specialisatie != null) {
            request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Specialisatie/Naam", specialisatie.naam());
            request.setValue("//Bewijsregistratie/Bewijs/Specialisatie/Code", specialisatie.code());
        } else {
            request.removeNode("//Bewijsregistratie/Bewijs/Specialisatie");
        }
        var detailOnderwerp = bewijsBasis.detailOnderwerp();
        if(detailOnderwerp != null) {
            request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/DetailOnderwerp/Naam", detailOnderwerp.naam());
            request.setValue("//Bewijsregistratie/Bewijs/DetailOnderwerp/Code", detailOnderwerp.code());
        } else {
            request.removeNode("//Bewijsregistratie/Bewijs/DetailOnderwerp");
        }
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/Vervalperiode", bewijsBasis.vervalperiode());
        request.setValueOrRemoveNode("//Bewijsregistratie/Bewijs/UrenVolwassenenonderwijs", Optional.ofNullable(bewijsBasis.urenVolwassenenonderwijs())
                .map(Object::toString)
                .orElse(null));

        request.removeNode("//Bewijsregistratie/Bewijs/AlternatieveInstanties");
        request.removeNode("//Bewijsregistratie/Bewijs/BijkomendeInformaties");

        var xml = request.getXml();
        var bewijsNode = request.xpath("//Bewijsregistratie/Bewijs").item(0);

        var alternatieveInstanties = bewijsBasis.alternatieveInstanties();
        if(alternatieveInstanties != null) {
            var alternatieveInstantiesNode = xml.createElement("AlternatieveInstanties");
            bewijsNode.appendChild(alternatieveInstantiesNode);

            for(var alternatieveInstantie : alternatieveInstanties) {
                var alternatieveInstantieNode = xml.createElement("AlternatieveInstantie");
                alternatieveInstantiesNode.appendChild(alternatieveInstantieNode);

                var instantierolNode = xml.createElement("Instantierol");
                alternatieveInstantieNode.appendChild(instantierolNode);
                var instantierolNaamNode = xml.createElement("Naam");
                instantierolNode.appendChild(instantierolNaamNode);
                instantierolNaamNode.appendChild(xml.createTextNode(alternatieveInstantie.instantierol().naam()));

                var instantieNode = xml.createElement("Instantie");
                alternatieveInstantieNode.appendChild(instantieNode);
                var instantieNaamNode = xml.createElement("Naam");
                instantieNode.appendChild(instantieNaamNode);
                instantieNaamNode.appendChild(xml.createTextNode(alternatieveInstantie.instantie().naam()));
            }
        }

        var bijkomendeInformaties = bewijsBasis.bijkomendeInformaties();
        if(bijkomendeInformaties != null) {
            var bijkomendeInformatiesNode = xml.createElement("BijkomendeInformaties");
            bewijsNode.appendChild(bijkomendeInformatiesNode);

            for(var bijkomendeInformatie : bijkomendeInformaties) {
                var bijkomendeInformatieNode = xml.createElement("BijkomendeInformatie");
                bijkomendeInformatiesNode.appendChild(bijkomendeInformatieNode);

                var naamNode = xml.createElement("Naam");
                bijkomendeInformatieNode.appendChild(naamNode);
                naamNode.appendChild(xml.createTextNode(bijkomendeInformatie.naam()));

                var inhoudNode = xml.createElement("Inhoud");
                bijkomendeInformatieNode.appendChild(inhoudNode);
                inhoudNode.appendChild(xml.createTextNode(bijkomendeInformatie.inhoud()));
            }
        }
    }
}