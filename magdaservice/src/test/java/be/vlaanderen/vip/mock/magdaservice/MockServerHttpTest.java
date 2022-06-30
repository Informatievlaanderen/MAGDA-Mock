package be.vlaanderen.vip.mock.magdaservice;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaSignedConnection;
import be.vlaanderen.vip.magda.client.MagdaSoapConnection;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.diensten.RegistreerUitschrijvingAanvraag;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsImpl;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
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

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(aanvraag.getRequestId().toString());

        var resultaat = doc.getValue("//Antwoorden/Antwoord/Inhoud/Resultaat");
        assertThat(resultaat).isEqualTo("1");

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
    }

    private MagdaEndpoints configureMagdaEndpoints(MagdaConfigDto config) {
        MagdaEndpoints magdaEndpoints = new MagdaEndpointsImpl(config);
        magdaEndpoints.addMapping("RegistreerInschrijving", "02.00.0000", "http://localhost:8080/RegistreerInschrijvingDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("RegistreerUitschrijving", "02.00.0000", "http://localhost:8080/RegistreerUitschrijvingDienst-02.00/soap/WebService");
        magdaEndpoints.addMapping("GeefBewijs", "02.00.0000", "http://localhost:8080/GeefBewijsDienst-02.00/soap/WebService");

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
