package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.diensten.GeefPasfotoAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GeefPasfotoTest extends MockTestBase {
    private static final String INSZ_MAGDA_OVERBELAST = "91010100144";
    private static final String INSZ_ECHTE_PASFOTO = "67021546719";
    private static final String INSZ_RANDOM_MAN = "67021400130" ;
    private static final String INSZ_RANDOM_VROUW = "67021400229" ;

    // Zet deze constante op true om de base64 geëncodeerde foto te bewaren in een temp jpeg bestand
    // De test print uit op welk pad de foto bewaard is.
    // Zet dit af voor continuous build
    public static final boolean STORE_FOTO_IN_TEMP_FILE = false;

    @Test
    @SneakyThrows
    void geefPasfotoVoorBestaandInszNummer() {
        assertPasfotoCorrect(INSZ_ECHTE_PASFOTO, 80065);
    }

    @Test
    @SneakyThrows
    void geefPasfotoVoorRandomMan() {
        assertPasfotoCorrect(INSZ_RANDOM_MAN, 28722);
    }

    @Test
    @SneakyThrows
    void geefPasfotoVoorRandomVrouw() {
        assertPasfotoCorrect(INSZ_RANDOM_VROUW, 32659);
    }

    private void assertPasfotoCorrect(String inszRandomMan, int expected) throws IOException {
        final String requestInsz = inszRandomMan;
        var aanvraag = new GeefPasfotoAanvraag(requestInsz);
        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(1);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(0);

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var base64Foto = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/Foto");
        var decoded = Base64.decodeBase64(base64Foto.getBytes());
        assertThat(decoded.length).isEqualTo(expected) ;

        storeImage(decoded);
    }

    private static void storeImage(byte[] decoded) throws IOException {
        if (STORE_FOTO_IN_TEMP_FILE) {
            File tempFile = File.createTempFile("mugshot", ".jpg", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(decoded);
            }
            System.out.println("Wrote file to " + tempFile.getAbsolutePath());
        }
    }

    @Test
    @SneakyThrows
    void geefPasfotov0200LuktNietOmdatMagdaOverbelastIs() {
        var aanvraag = new GeefPasfotoAanvraag(INSZ_MAGDA_OVERBELAST);

        AfnemerLogServiceMock afnemerLogService = new AfnemerLogServiceMock();

        MagdaConnectorImpl connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(aanvraag);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, aanvraag);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getAanvragen()).isEqualTo(1);
        assertThat(afnemerLogService.getGeslaagd()).isEqualTo(0);
        assertThat(afnemerLogService.getGefaald()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }


}
