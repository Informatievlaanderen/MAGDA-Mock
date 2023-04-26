package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.simulators.*;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import java.util.Optional;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {

    private Document defaultResponse = null;
    
    private SOAPSimulator simulator;

    public MagdaMockConnection(SOAPSimulator simulator) {
        this.simulator = simulator;
    }
    
//    public MagdaMockConnection(ResourceFinder finder, TwoWaySslProperties requestVerifierConfig, TwoWaySslProperties responseSignerConfig) throws TwoWaySslException {
//        Optional<DocumentSignatureVerifier> requestVerifier;
//        Optional<DocumentSigner> responseSigner;
//
//        if(requestVerifierConfig != null && StringUtils.isNotEmpty(requestVerifierConfig.getKeyStoreLocation())) {
//            requestVerifier = Optional.of(DocumentSignatureVerifier.fromJksStore(requestVerifierConfig));
//        } else {
//            requestVerifier = Optional.empty();
//        }
//
//        if(responseSignerConfig != null && StringUtils.isNotEmpty(responseSignerConfig.getKeyStoreLocation())) {
//            responseSigner = Optional.of(DocumentSigner.fromJksStore(responseSignerConfig));
//        } else {
//            responseSigner = Optional.empty();
//        }
//
//        this.simulator = constructBuiltInSimulator(finder, requestVerifier, responseSigner);
//    }

    private static SOAPSimulator constructBuiltInSimulator(ResourceFinder finder, Optional<DocumentSignatureVerifier> requestVerifier, Optional<DocumentSigner> responseSigner) {
        var builder = SOAPSimulatorBuilder.builder(finder)
                                          .magdaMockSimulator();
        
        builder = requestVerifier.map(builder::requestVerifier)
                                 .orElse(builder);
        builder = responseSigner.map(builder::responseSigner)
                                .orElse(builder);
        
        return builder.build();
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        log.info("Answering using MAGDA Mock");

        if (defaultResponse != null) {
            Document answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        return send(xml);
    }

    private Document send(Document xml) throws MagdaSendFailed {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
