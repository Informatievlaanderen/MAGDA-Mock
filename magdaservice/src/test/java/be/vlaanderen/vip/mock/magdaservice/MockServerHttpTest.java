package be.vlaanderen.vip.mock.magdaservice;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaSignedConnection;
import be.vlaanderen.vip.magda.client.MagdaSoapConnection;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsImpl;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magdaservice.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Socket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@EnabledIf("mockServerIsRunning")
@SpringBootTest
// These tests only run if there is a (mock) server running on port 8080
public class MockServerHttpTest {


    public static final String CORRECT_INSZ = "67021546719";
    public static final String INSZ_MAGDA_OVERBELAST = "91010100144";

    @Test
    @SneakyThrows
    void callGeefBewijs() {
        MagdaConfigDto config = configureMagdaParameters();
        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new GeefBewijsAanvraag(CORRECT_INSZ);
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/BewijsrefertesLed/Bewijsreferte");
        assertThat(resultaat).isEqualTo("66567d75-a223-4c12-959e-6560e3d0f0e5");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijving() {
        MagdaConfigDto config = configureMagdaParameters();

        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new RegistreerInschrijvingAanvraag(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerInschrijvingFaaltMagdaOverbelast() {
        MagdaConfigDto config = configureMagdaParameters();

        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        var uitzondering = antwoord.getUitzonderingen().get(0) ;
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT) ;
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996") ;
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen") ;
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijving() {
        MagdaConfigDto config = configureMagdaParameters();

        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new RegistreerUitschrijvingAanvraag(CORRECT_INSZ, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void callRegistreerUitschrijvingFaaltMagdaOverbelast() {
        MagdaConfigDto config = configureMagdaParameters();

        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new RegistreerUitschrijvingAanvraag(INSZ_MAGDA_OVERBELAST, LocalDate.now(), LocalDate.now().plus(7, ChronoUnit.DAYS));
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);

        var uitzondering = antwoord.getUitzonderingen().get(0) ;
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT) ;
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996") ;
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen") ;
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelasting() {
        MagdaConfigDto config = configureMagdaParameters();
        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag("82102108114");
        var request = MagdaDocument.fromTemplate(aanvraag);
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", "82102108114");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");
    }

    @Test
    @SneakyThrows
    void callGeefPersoonMetCustomRequest() {
        MagdaConfigDto config = configureMagdaParameters();
        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var request = MagdaDocument.fromResource(MockServerHttpTest.class,"/requests/GeefPersoonRequest.xml");

        var aanvraag = new GeefPersoonAanvraag("00000099504");
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/Persoon/INSZ", "00000099504");
    }

    @Test
    @SneakyThrows
    void callGeefAanslagbiljetPersonenbelastingMetCustomRequest() {
        MagdaConfigDto config = configureMagdaParameters();
        MagdaEndpoints magdaEndpoints = configureMagdaEndpoints(config);

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config, "magdamock.service.integrationtest");
        var soapConnection = new MagdaSoapConnection(magdaEndpoints, config);
        var signatureConnection = new MagdaSignedConnection(soapConnection, config);
        var connector = new MagdaConnectorImpl(signatureConnection, afnemerLog, magdaEndpoints, hoedanigheid);

        var request = MagdaDocument.fromResource(MockServerHttpTest.class,"/requests/GeefAanslagbiljetPersonenbelastingRequest.xml");

        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag("82102108114");
        var antwoord = connector.send(aanvraag, request);
        log.info("Antwoord : {}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();

        var doc = antwoord.getDocument();

        var afzenderReferte = doc.getValue("//Repliek/Context/Bericht/Ontvanger/Referte");
        assertThat(afzenderReferte).isEqualTo(aanvraag.getRequestId().toString());

        var antwoordReferte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(antwoordReferte).isEqualTo(aanvraag.getRequestId().toString());

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", "82102108114");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");
    }


    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        String value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }


    private MagdaEndpoints configureMagdaEndpoints(MagdaConfigDto config) {
        MagdaEndpoints magdaEndpoints = new MagdaEndpointsImpl(config);
        magdaEndpoints.addMapping("RegistreerInschrijving", "02.00.0000", "http://localhost:8080/RegistreerInschrijvingDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("RegistreerUitschrijving", "02.00.0000", "http://localhost:8080/RegistreerUitschrijvingDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("GeefBewijs", "02.00.0000", "http://localhost:8080/GeefBewijsDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("GeefAanslagbiljetPersonenbelasting", "02.00.0000", "http://localhost:8080/GeefAanslagbiljetPersonenbelastingDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("GeefPersoon", "02.02.0000", "http://localhost:8080/GeefPersoonDienst-02.02/soap/WebService");

        return magdaEndpoints;
    }

    private MagdaConfigDto configureMagdaParameters() {
        MagdaConfigDto config = new MagdaConfigDto();
        config.setKeystore(new TwoWaySslProperties());
        config.setEnvironment("http://localhost:8080");
        config.getRegistration().put("default", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-mock").capacity("1234").build());
        config.getRegistration().put("custom", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-custom-mock").capacity("5678").build());
        return config;
    }

    private static boolean checked = false;
    private static boolean mockServerRunning = false;

    public static boolean somebodyListeningOn(String host, int port) {
        boolean ret = false;
        try {
            Socket s = new Socket(host, port);
            ret = true;
            s.close();
        } catch (Exception e) {
            //
        }
        return ret;
    }

    public static boolean mockServerIsRunning() {
        if (!checked) {
            System.out.println("Checking if MagdaMock Server is running on this machine");
            mockServerRunning = somebodyListeningOn("localhost", 8080);
            if (mockServerRunning) {
                System.out.println("MagdaMock available. All server tests enabled");
            } else {
                System.err.println("MagdaMock unavailable. All server tests disabled");
            }
            checked = true;
        }
        return mockServerRunning;
    }
}
