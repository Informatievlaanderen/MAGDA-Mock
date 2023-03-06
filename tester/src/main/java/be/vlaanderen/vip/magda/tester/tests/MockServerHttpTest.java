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
import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@SpringBootTest
public class MockServerHttpTest extends MockServerTest {

    private static final String CORRECT_INSZ = "67021546719";
    private static final String INSZ_MAGDA_OVERBELAST = "91010100144";
    private static final String INSZ_ECHTE_PASFOTO = "67021546719";
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
        var magdaEndpoints = new MockMagdaEndpoints(URI.create(magdaConfigDto.getEnvironment())); // XXX environment is semantically the wrong thing to pass here
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(magdaConfigDto, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, magdaConfigDto);
        var signatureConnection = new MagdaSignedConnection(soapConnection, magdaConfigDto);
        connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, hoedanigheid);
    }

    @Test
    @SneakyThrows
    void callGeefBewijs() {
        var aanvraag = new GeefBewijsAanvraag(CORRECT_INSZ);

        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

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
        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

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
        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijving() {
        var aanvraag = new RegistreerUitschrijvingAanvraag(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

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
        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

        assertResponsBevatUitzondering(antwoord, TypeUitzondering.FOUT, "99996", "Te veel gelijktijdige bevragingen");
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelasting() {
        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag("82102108114");
        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

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
        log.debug("Antwoord : {}", antwoord.getDocument());

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
        log.debug("Antwoord : {}", antwoord.getDocument());

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

    @Test
    @SneakyThrows
    void multipleCalls() {
        var aanvraag = new GeefBewijsAanvraag(CORRECT_INSZ);

        assertDoesNotThrow(() -> connector.send(aanvraag));
        assertDoesNotThrow(() -> connector.send(aanvraag));
    }

    @SneakyThrows
    private void assertPasfotoCorrect(String requestInsz, int expected) {
        var aanvraag = new GeefPasfotoAanvraag(requestInsz);

        var antwoord = connector.send(aanvraag);
        log.debug("Antwoord : {}", antwoord.getDocument());

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
            log.debug("Wrote file to " + tempFile.getAbsolutePath());
        }
    }

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        String value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    private MagdaConfigDto configureMagdaParameters() {
        var magdaConfigDto = new MagdaConfigDto();
        if(wssConfig.isWssEnabled()) {
            magdaConfigDto.setKeystore(wssConfig.getKeystoreProperties());
        } else {
            magdaConfigDto.setKeystore(new TwoWaySslProperties());
        }
        magdaConfigDto.setVerificationEnabled(false); // TODO might enable this again when verification works as prescribed
        magdaConfigDto.setEnvironment(testerConfig.getServiceUrl() + "/Magda-02.00/soap/WebService");
        magdaConfigDto.getRegistration().put("default", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-mock").build());
        magdaConfigDto.getRegistration().put("custom", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-custom-mock").build());

        return magdaConfigDto;
    }
}
