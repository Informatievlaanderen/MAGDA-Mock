package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefPasfotoRequest;
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
    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_ECHTE_PASFOTO = "67621546751";
    private static final String INSZ_RANDOM_MAN = "67021400130";
    private static final String INSZ_RANDOM_VROUW = "67021400229";

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
        assertPasfotoCorrect(INSZ_RANDOM_MAN, 30271);
    }

    @Test
    @SneakyThrows
    void geefPasfotoVoorRandomVrouw() {
        assertPasfotoCorrect(INSZ_RANDOM_VROUW, 34052);
    }

    private void assertPasfotoCorrect(String requestInsz, int expected) throws IOException {
        var request = GeefPasfotoRequest.builder()
                .insz(requestInsz)
                .build();
        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(request.getRequestId().toString());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var base64Foto = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/Foto");
        var decoded = Base64.decodeBase64(base64Foto.getBytes());
        assertThat(decoded).hasSize(expected);

        storeImage(decoded);
    }

    private static void storeImage(byte[] decoded) throws IOException {
        if (STORE_FOTO_IN_TEMP_FILE) {
            var tempFile = File.createTempFile("passport_photo", ".jpg", null);
            try (var fos = new FileOutputStream(tempFile)) {
                fos.write(decoded);
            }
            System.out.println("Wrote file to " + tempFile.getAbsolutePath());
        }
    }

    @Test
    @SneakyThrows
    void geefPasfotov0200LuktNietOmdatMagdaOverbelastIs() {
        var request = GeefPasfotoRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }
}