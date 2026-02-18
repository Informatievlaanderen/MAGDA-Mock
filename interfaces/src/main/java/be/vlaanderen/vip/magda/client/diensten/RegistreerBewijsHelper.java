package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Beroepskwalificatie;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Deelkwalificatie;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

public final class RegistreerBewijsHelper {

    private RegistreerBewijsHelper(){}

    public static void voegBeroepskwalificatiesToe(Document xml, Node bewijsNode, List<Beroepskwalificatie> beroepskwalificaties) {
        if (beroepskwalificaties != null) {
            var beroepskwalificatiesNode = xml.createElement("Beroepskwalificaties");
            bewijsNode.appendChild(beroepskwalificatiesNode);

            for (var beroepskwalificatie : beroepskwalificaties) {
                var beroepskwalificatieNode = xml.createElement("Beroepskwalificatie");
                beroepskwalificatiesNode.appendChild(beroepskwalificatieNode);

                var naamNode = xml.createElement("Naam");
                beroepskwalificatieNode.appendChild(naamNode);
                naamNode.appendChild(xml.createTextNode(beroepskwalificatie.naam()));

                var codeNode = xml.createElement("Code");
                beroepskwalificatieNode.appendChild(codeNode);
                codeNode.appendChild(xml.createTextNode(beroepskwalificatie.code()));
            }
        }
    }

    public static void voegDeelkwalificatiesToe(Document xml, Node bewijsNode, List<Deelkwalificatie> deelkwalificaties) {
        if (deelkwalificaties != null) {
            var deelkwalificatiesNode = xml.createElement("Deelkwalificaties");
            bewijsNode.appendChild(deelkwalificatiesNode);

            for (var deelkwalificatie : deelkwalificaties) {
                var deelkwalificatieNode = xml.createElement("Deelkwalificatie");
                deelkwalificatiesNode.appendChild(deelkwalificatieNode);

                var naamNode = xml.createElement("Naam");
                deelkwalificatieNode.appendChild(naamNode);
                naamNode.appendChild(xml.createTextNode(deelkwalificatie.naam()));

                var codeNode = xml.createElement("Code");
                deelkwalificatieNode.appendChild(codeNode);
                codeNode.appendChild(xml.createTextNode(deelkwalificatie.code()));
            }
        }
    }
}
