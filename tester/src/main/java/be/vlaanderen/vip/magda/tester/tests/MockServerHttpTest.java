package be.vlaanderen.vip.magda.tester.tests;

import be.vlaanderen.vip.magda.client.*;
import be.vlaanderen.vip.magda.client.diensten.*;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.magda.tester.config.MockMagdaEndpoints;
import be.vlaanderen.vip.magda.tester.config.WssConfig;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@SpringBootTest
public class MockServerHttpTest extends MockServerTest {

    private static final String CORRECT_INSZ = "67621546751";
    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_ECHTE_PASFOTO = "67621546751";
    private static final String INSZ_RANDOM_MAN = "67021400130";
    private static final String INSZ_RANDOM_VROUW = "67021400229";

    // Zet deze constante op true om de base64 geëncodeerde foto te bewaren in een temp jpeg bestand
    // De test print uit op welk pad de foto bewaard is.
    // Zet dit af voor continuous build
    public static final boolean STORE_FOTO_IN_TEMP_FILE = false;

    @Autowired
    private WssConfig wssConfig;

    private MagdaConnector connector;

    @BeforeAll
    @SneakyThrows
    void beforeAll() {
        assertServiceAvailable();
    }

    @BeforeEach
    @SneakyThrows
    void setup() {
        var afnemerLog = new AfnemerLogServiceMock();
        var magdaConfigDto = configureMagdaParameters();
        var magdaEndpoints = makeMockEndpoints();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(magdaConfigDto);
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, magdaConfigDto);
        var signatureConnection = new MagdaSignedConnection(soapConnection, magdaConfigDto);
        connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, hoedanigheid);
    }

    @Test
    @SneakyThrows
    void callGeefBewijs() {
        var request = new GeefBewijsRequest(CORRECT_INSZ);

        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, request.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/BewijsrefertesLed/Bewijsreferte");
        assertThat(resultaat).isEqualTo("66567d75-a223-4c12-959e-6560e3d0f0e5");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijving() {
        var request = new RegistreerInschrijvingRequest(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, request.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijvingFaaltMagdaOverbelast() {
        var request = new RegistreerInschrijvingRequest(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijving() {
        var request = new RegistreerUitschrijvingRequest(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, request.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijvingFaaltMagdaOverbelast() {
        var request = new RegistreerUitschrijvingRequest(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelasting() {
        var request = new GeefAanslagbiljetPersonenbelastingRequest("82102108114");
        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, request.getRequestId());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", "82102108114");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");
    }

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

    @Test
    @SneakyThrows
    void multipleCalls() {
        var request = new GeefBewijsRequest(CORRECT_INSZ);

        assertDoesNotThrow(() -> connector.send(request));
        assertDoesNotThrow(() -> connector.send(request));
    }

    @SneakyThrows
    private void assertPasfotoCorrect(String requestInsz, int expected) {
        var request = new GeefPasfotoRequest(requestInsz);

        var antwoord = connector.send(request);
        logMagdaAntwoord(antwoord);

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, request.getRequestId());

        var insz = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ");
        assertThat(insz).isEqualTo(requestInsz);

        var base64Foto = doc.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/Foto");
        var decoded = Base64.decodeBase64(base64Foto.getBytes());
        assertThat(decoded.length).isEqualTo(expected);

        storeImage(decoded);
    }

    private void assertResponsBevatAntwoord(MagdaAntwoord antwoord) {
        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
    }

    private void assertResponsBevatUitzondering(MagdaAntwoord antwoord, TypeUitzondering exptectedType, String expectedFoutCode, String expectedDiagnose) {
        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(exptectedType);
        assertThat(uitzondering.getIdentificatie()).isEqualTo(expectedFoutCode);
        assertThat(uitzondering.getDiagnose()).isEqualTo(expectedDiagnose);
    }

    private void assertResponsKomtOvereenMetRequest(MagdaDocument doc, UUID requestId) {
        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(requestId.toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(requestId.toString());
    }

    private static void storeImage(byte[] decoded) throws IOException {
        if (STORE_FOTO_IN_TEMP_FILE) {
            var tempFile = File.createTempFile("mugshot", ".jpg", null);
            try (var fos = new FileOutputStream(tempFile)) {
                fos.write(decoded);
            }
            log.debug("Wrote file to " + tempFile.getAbsolutePath());
        }
    }

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        var value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    private MockMagdaEndpoints makeMockEndpoints() {
        return new MockMagdaEndpoints(testerConfig.getServiceUrl().resolve("/Magda-02.00/soap/WebService"));
    }

    private MagdaConfigDto configureMagdaParameters() {
        var magdaConfigDto = new MagdaConfigDto();
        if(wssConfig.isWssEnabled()) {
            magdaConfigDto.setKeystore(wssConfig.getKeystoreProperties());
        } else {
            magdaConfigDto.setKeystore(new TwoWaySslProperties());
        }
        magdaConfigDto.setVerificationEnabled(false); // TODO might enable this again when verification works as prescribed
        magdaConfigDto.getRegistration().put("default", MagdaRegistrationConfigDto.builder().identification("kb.vlaanderen.be/aiv/burgerloket-wwoom-mock").build());
        magdaConfigDto.getRegistration().put("custom", MagdaRegistrationConfigDto.builder().identification("kb.vlaanderen.be/aiv/burgerloket-wwoom-custom-mock").build());

        return magdaConfigDto;
    }

    private void logMagdaAntwoord(MagdaAntwoord antwoord) {
        log.debug("Response : {}", antwoord.getDocument());
    }
}
