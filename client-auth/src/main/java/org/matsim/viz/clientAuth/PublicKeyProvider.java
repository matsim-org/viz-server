package org.matsim.viz.clientAuth;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PublicKeyProvider implements RSAKeyProvider {

    private static final Logger logger = LoggerFactory.getLogger(PublicKeyProvider.class);

    private Map<String, RSAPublicKey> cachedKeys = new HashMap<>();
    private Client client;
    private URI certificateEndpoint;
    private KeyFactory keyFactory;

    PublicKeyProvider(Client client, URI idProvider) {
        this.client = client;
        this.certificateEndpoint = idProvider.resolve("/certificates");
        try {
            this.keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {

        if (cachedKeys.containsKey(keyId))
            return cachedKeys.get(keyId);

        fetchKeys();
        return cachedKeys.get(keyId);
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        throw new InternalException("Public Key Provider may only used to fetch public keys for verification");
    }

    @Override
    public String getPrivateKeyId() {
        throw new InternalException("Public Key Provider may only used to fetch public keys for verification");
    }

    private void fetchKeys() {

        KeyInformation[] keyInformation;
        try {
            logger.info("Fetching key information from " + certificateEndpoint.toString());
            keyInformation = client.target(certificateEndpoint).request().get(KeyInformation[].class);
        } catch (RuntimeException e) {
            logger.error("Couldn't fetch keys from " + certificateEndpoint.toString() + " will try again later");
            return;
        }

        logger.info("Received " + keyInformation.length + " keys. generating new Key cache.");
        Map<String, RSAPublicKey> newKeys = new HashMap<>();

        for (KeyInformation information : keyInformation) {
            try {
                RSAPublicKey publicKey = createKey(information);
                newKeys.put(information.getKid(), publicKey);
            } catch (Exception e) {
                // if we can't parse one key try to parse the others.
                logger.error(e.getMessage());
            }
        }
        this.cachedKeys = newKeys;
    }

    private RSAPublicKey createKey(KeyInformation keyInformation) {
        if (!canProcessKey(keyInformation))
            throw new InvalidInputException("could not process fetched key information");

        try {
            byte[] data = Base64.getDecoder().decode(keyInformation.getN());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean canProcessKey(PublicKeyProvider.KeyInformation keyInformation) {
        return "RS512".equals(keyInformation.alg) && "RSA".equals(keyInformation.kty)
                && "sig".equals(keyInformation.use) && StringUtils.isNotBlank(keyInformation.kid) &&
                StringUtils.isNotBlank(keyInformation.n);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    static class KeyInformation {

        private String kid;
        private String n;
        private String alg;
        private String use;
        private String kty;
    }
}
