package be.vlaanderen.vip.mock.magdaservice;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaSignedConnection;
import be.vlaanderen.vip.magda.client.MagdaSoapConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsImpl;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.config.MagdaRegistrationConfigDto;
import be.vlaanderen.vip.mock.magdaservice.aanvraag.GeefBewijsAanvraag;
import be.vlaanderen.vip.mock.magdaservice.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Socket;

@Slf4j
@EnabledIf("mockServerIsRunning")
@SpringBootTest
// These tests only run if there is a (mock) server running on port 8080
public class MockServerHttpTest {


    @Test
    @SneakyThrows
    void canCallMockServerWithMagdaConnection() {
        MagdaConfigDto config = new MagdaConfigDto();
        config.setKeystore(new TwoWaySslProperties());
        config.setEnvironment("http://localhost:8080");
        config.getRegistration().put("default", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-mock").capacity("1234").build());
        config.getRegistration().put("custom", MagdaRegistrationConfigDto.builder().uri("kb.vlaanderen.be/aiv/burgerloket-wwoom-custom-mock").capacity("5678").build());

        MagdaEndpoints magdaEndpoints = new MagdaEndpointsImpl(config);
        magdaEndpoints.addMapping("GeefBewijs","02.00.0000","http://localhost:8080/GeefBewijsDienst-02.00/soap/WebService");

        var afnemerLog = new AfnemerLogServiceMock();
        var hoedanigheid = new MagdaHoedanigheidServiceImpl(config,"magdamock.service.integrationtest") ;
        var soapConnection = new MagdaSoapConnection(magdaEndpoints,config) ;
        var signatureConnection = new MagdaSignedConnection(soapConnection,config) ;
        var connector = new MagdaConnectorImpl(signatureConnection,afnemerLog,magdaEndpoints,hoedanigheid) ;

        final String requestInsz = "67021546719";
        var aanvraag = new GeefBewijsAanvraag(requestInsz);
        var request = MagdaDocument.fromTemplate(aanvraag) ;
        var antwoord = connector.send(aanvraag,request) ;
        log.info("Antwoord : {}",antwoord.getDocument());
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
