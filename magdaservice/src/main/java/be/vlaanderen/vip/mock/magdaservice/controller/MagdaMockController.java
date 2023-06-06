package be.vlaanderen.vip.mock.magdaservice.controller;


import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MagdaMockController {
    // Gemeenschappelijk endpoint voor alle MAGDA SOAP 2.0 webservices
    private static final String MAGDA_SOAP_02_00 = "Magda-02.00/soap/WebService";

    private MagdaConnection mockConnection;

    public MagdaMockController(MagdaConnection mockConnection) {
        this.mockConnection = mockConnection;
    }

    @PostMapping(value = MAGDA_SOAP_02_00, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> magdaSoap0200WebService(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }


    private ResponseEntity<String> processMagdaMockRequest(String request) throws MagdaSendFailed {
        //TODO: handle request parsing errors and return Magda Uitzondering error
        MagdaDocument aanvraag = MagdaDocument.fromString(request);

        var antwoord = mockConnection.sendDocument(aanvraag.getXml());
        if (antwoord != null) {
            var doc = MagdaDocument.fromDocument(antwoord);
            final ResponseEntity<String> response = parseInputstream(doc);

            return response;

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<String> parseInputstream(MagdaDocument magdaDocument) {
        if (magdaDocument != null) {
              return ResponseEntity.ok().contentType(APPLICATION_XML).body(magdaDocument.toString());
        } else {
            log.error("Could not find XML");

            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }
}
