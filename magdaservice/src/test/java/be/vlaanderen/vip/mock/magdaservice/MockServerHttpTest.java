package be.vlaanderen.vip.mock.magdaservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import be.vlaanderen.vip.magda.client.MagdaAntwoord;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaSignedConnection;
import be.vlaanderen.vip.magda.client.MagdaSoapConnection;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefPasfotoAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magdaservice.config.MockMagdaEndpoints;
import be.vlaanderen.vip.mock.magdaservice.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnabledIf("mockServerIsRunning")
@SpringBootTest
// These tests only run if there is a (mock) server running on port 8080
public class MockServerHttpTest extends MockServerTest {


    public static final String CORRECT_INSZ = "67021546719";
    public static final String INSZ_MAGDA_OVERBELAST = "91010100144";
    private static final String INSZ_ECHTE_PASFOTO = "67021546719";
    private static final String INSZ_RANDOM_MAN = "67021400130";
    private static final String INSZ_RANDOM_VROUW = "67021400229";


    // Zet deze constante op true om de base64 geëncodeerde foto te bewaren in een temp jpeg bestand
    // De test print uit op welk pad de foto bewaard is.
    // Zet dit af voor continuous build
    public static final boolean STORE_FOTO_IN_TEMP_FILE = false;


    private AfnemerLogServiceMock afnemerLog;
    private MagdaConnectorImpl connector;

    @BeforeEach
    @SneakyThrows
    void setup() {
        afnemerLog = new AfnemerLogServiceMock();
        var config = configureMagdaParameters();
        var magdaEndpoints = new MockMagdaEndpoints(config.getEnvironment());
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);
    }

    @Test
    @SneakyThrows
    void callGeefBewijs() {
        var aanvraag = new GeefBewijsAanvraag(CORRECT_INSZ);

        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/BewijsrefertesLed/Bewijsreferte");
        assertThat(resultaat).isEqualTo("66567d75-a223-4c12-959e-6560e3d0f0e5");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijving() {
        var aanvraag = new RegistreerInschrijvingAanvraag(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijvingFaaltMagdaOverbelast() {
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijving() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijvingFaaltMagdaOverbelast() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelasting() {
        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag("82102108114");
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", "82102108114");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");
    }

    @Test
    @SneakyThrows
    void callGeefPersoonMetCustomRequest() {
        var request = MagdaDocument.fromResource(MockServerHttpTest.class, "/requests/GeefPersoonRequest.xml");

        var aanvraag = new GeefPersoonAanvraag("00000099504");
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/Persoon/INSZ", "00000099504");
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelastingMetCustomRequest() {
        var request = MagdaDocument.fromResource(MockServerHttpTest.class, "/requests/GeefAanslagbiljetPersonenbelastingRequest.xml");

        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag("82102108114");
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

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
        assertPasfotoCorrect(INSZ_RANDOM_MAN, 28722);
    }

    @Test
    @SneakyThrows
    void geefPasfotoVoorRandomVrouw() {
        assertPasfotoCorrect(INSZ_RANDOM_VROUW, 32659);
    }

    @SneakyThrows
    private void assertPasfotoCorrect(String inszRandomMan, int expected) throws IOException {
        final String requestInsz = inszRandomMan;
        var aanvraag = new GeefPasfotoAanvraag(requestInsz);

        var request = MagdaDocument.fromTemplate(aanvraag);

        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatAntwoord(antwoord);

        var doc = antwoord.getDocument();

        assertResponsKomtOvereenMetRequest(doc, aanvraag.getRequestId());

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
            File tempFile = File.createTempFile("mugshot", ".jpg", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(decoded);
            }
            System.out.println("Wrote file to " + tempFile.getAbsolutePath());
        }
    }


    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        String value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }


    private MagdaConfigDto configureMagdaParameters() {
        MagdaConfigDto config = new MagdaConfigDto();
        config.setKeystore(new TwoWaySslProperties());
        config.setEnvironment("http://localhost:8080/Magda-02.00/soap/WebService");
        config.getRegistration().put("default", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-mock").capacity("1234").build());
        config.getRegistration().put("custom", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-custom-mock").capacity("5678").build());
        return config;
    }

}
