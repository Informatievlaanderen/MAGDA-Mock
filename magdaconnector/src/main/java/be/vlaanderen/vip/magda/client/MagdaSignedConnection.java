package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.security.*;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.w3c.dom.Document;

@Slf4j
public class MagdaSignedConnection implements MagdaConnection {
    private final MagdaConnection magdaConnection;
    @Nullable private final DocumentSigner requestSigner;
    @Nullable private final DocumentSignatureVerifier responseVerifier;

    public MagdaSignedConnection(MagdaConnection magdaConnection, MagdaConfigDto config) throws TwoWaySslException {
        this(magdaConnection, config.getKeystore(), config.isVerificationEnabled() ? config.getKeystore() : null); // TODO will need some more work for verification to actually work as prescribed
    }

    public MagdaSignedConnection(MagdaConnection magdaConnection, TwoWaySslProperties requestSignerConfig, TwoWaySslProperties responseVerifierConfig) throws TwoWaySslException {
        this.magdaConnection = magdaConnection;

        if(requestSignerConfig != null && StringUtils.isNotEmpty(requestSignerConfig.getKeyStoreLocation())) {
            this.requestSigner = DocumentSigner.fromJksStore(requestSignerConfig);
        } else {
            this.requestSigner = null;
        }

        if(responseVerifierConfig != null && StringUtils.isNotEmpty(responseVerifierConfig.getKeyStoreLocation())) {
            this.responseVerifier = DocumentSignatureVerifier.fromJksStore(responseVerifierConfig);
        } else {
            this.responseVerifier = null;
        }
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaConnectionException {
        try {
            if(requestSigner != null) {
                log.info("Signing request document...");
                requestSigner.signDocument(xml);
            }
        } catch(WSSecurityException e) {
            throw new MagdaConnectionException("MAGDA request kon niet worden gesigned", e);
        }

        var response = magdaConnection.sendDocument(xml);

        try {
            if(responseVerifier != null) {
                log.info("Verifying response document...");
                responseVerifier.verifyDocument(response);
            }
        } catch(InvalidSignatureException e) {
            throw new MagdaConnectionException("MAGDA antwoord niet correct gesigned", e);
        }

        return response;
    }
}
