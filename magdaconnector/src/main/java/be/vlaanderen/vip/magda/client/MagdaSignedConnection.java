package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.rest.MagdaRestRequest;
import be.vlaanderen.vip.magda.client.security.*;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.w3c.dom.Document;

import java.net.URISyntaxException;
import java.util.Optional;

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
            throw new MagdaConnectionException("MAGDA request could not be signed", e);
        }

        var response = magdaConnection.sendDocument(xml);

        try {
            if(responseVerifier != null) {
                log.info("Verifying response document...");
                responseVerifier.verifyDocument(response);
            }
        } catch(InvalidSignatureException e) {
            throw new MagdaConnectionException("MAGDA response could not be verified", e);
        }

        return response;
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(MagdaRestRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public Pair<JsonNode, Integer> sendRestRequest(String path, String query, String method, String requestBody) {
        throw new NotImplementedException();
    }
}
