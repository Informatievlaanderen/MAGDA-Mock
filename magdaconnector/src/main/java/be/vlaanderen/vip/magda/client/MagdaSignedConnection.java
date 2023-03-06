package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.InvalidSignatureException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.w3c.dom.Document;

import java.util.Optional;

@Slf4j
public class MagdaSignedConnection implements MagdaConnection {
    private final MagdaConnection magdaConnection;
    private final Optional<DocumentSigner> requestSigner;
    private final Optional<DocumentSignatureVerifier> responseVerifier;

    public MagdaSignedConnection(MagdaConnection magdaConnection, MagdaConfigDto config) throws WSSecurityException {
        this(magdaConnection, config.getKeystore(), config.isVerificationEnabled() ? config.getKeystore() : null); // TODO will need some more work for verification to actually work as prescribed
    }

    public MagdaSignedConnection(MagdaConnection magdaConnection, TwoWaySslProperties requestSignerConfig, TwoWaySslProperties responseVerifierConfig) throws WSSecurityException {
        this.magdaConnection = magdaConnection;

        if(requestSignerConfig != null && StringUtils.isNotEmpty(requestSignerConfig.getKeyStoreLocation())) {
            this.requestSigner = Optional.of(DocumentSigner.fromJksStore(requestSignerConfig));
        } else {
            this.requestSigner = Optional.empty();
        }

        if(responseVerifierConfig != null && StringUtils.isNotEmpty(responseVerifierConfig.getKeyStoreLocation())) {
            this.responseVerifier = Optional.of(DocumentSignatureVerifier.fromJksStore(responseVerifierConfig));
        } else {
            this.responseVerifier = Optional.empty();
        }
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        try {
            if(requestSigner.isPresent()) {
                log.info("Signing request document...");
                requestSigner.get().signDocument(xml);
            }
        } catch(WSSecurityException e) {
            throw new MagdaSendFailed("MAGDA request kon niet worden gesigned", e);
        }

        var response = magdaConnection.sendDocument(xml);

        try {
            if(responseVerifier.isPresent()) {
                log.info("Verifying response document...");
                responseVerifier.get().verifyDocument(response);
            }
        } catch(InvalidSignatureException e) {
            throw new MagdaSendFailed("MAGDA antwoord niet correct gesigned", e);
        }

        return response;
    }
}
