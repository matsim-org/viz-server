package org.matsim.viz.clientAuth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.matsim.viz.error.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OAuthAuthenticator<P extends Principal> implements Authenticator<String, P> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticator.class);

    private Map<String, Algorithm> keys = new HashMap<>();

    private Client client;
    private URI certificateEndpoint;
    private URI idProvider;
    private Function<AuthenticationResult, Optional<P>> principalProider;
    private KeyFactory keyFactory;

    public OAuthAuthenticator(Client client, URI idProvider, Function<AuthenticationResult,
            Optional<P>> principalProvider) {
        this.certificateEndpoint = idProvider.resolve("/certificates");
        this.idProvider = idProvider;
        this.client = client;
        this.principalProider = principalProvider;
        try {
            this.keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.fetchKeys();
    }

    @Override
    public Optional<P> authenticate(String token) {

        DecodedJWT decodedToken;
        try {
            decodedToken = JWT.decode(token);
            if (!keys.containsKey(decodedToken.getKeyId())) {
                fetchKeys();
                if (!keys.containsKey(decodedToken.getKeyId())) {
                    // if the key id is still not present after re fetching the keys
                    logger.info("Denying authentication for token: " + token);
                    logger.info("could not find key-id.");
                    return Optional.empty();
                }
            }

            // here we can be sure to have the key in our cache
            Algorithm algorithm = keys.get(decodedToken.getKeyId());
            JWT.require(algorithm)
                    .withIssuer(idProvider.toString())
                    .acceptExpiresAt(Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli())
                    .build().verify(token);
            AuthenticationResult result = new AuthenticationResult(decodedToken.getSubject(), decodedToken.getClaim("scope").asString());
            return principalProider.apply(result);

        } catch (RuntimeException e) {
            logger.info("Denying authentication for token: " + token);
            return Optional.empty();
        }
    }

    private void fetchKeys() {

        KeyInformation[] keyInformations;
        try {
            logger.info("Fetching key information from " + certificateEndpoint.toString());
            keyInformations = client.target(certificateEndpoint).request().get(KeyInformation[].class);
        } catch (RuntimeException e) {
            logger.error("Couldn't fetch keys from " + certificateEndpoint.toString() + " will try again later");
            return;
        }

        logger.info("Received " + keyInformations.length + " keys. generating new Key cache.");
        Map<String, Algorithm> newKeys = new HashMap<>();

        for (KeyInformation information : keyInformations) {
            try {
                Algorithm algorithm = createAlgorithm(information);
                newKeys.put(information.getKid(), algorithm);
            } catch (Exception e) {
                // if we can't parse one key try to parse the others.
                logger.error(e.getMessage());
            }
        }
        this.keys = newKeys;
    }

    private Algorithm createAlgorithm(KeyInformation keyInformation) {

        if (!canProcessKey(keyInformation)) {
            throw new InvalidInputException("could not process key information from endpoint " + certificateEndpoint.toString());
        }
        try {
            byte[] data = Base64.getDecoder().decode(keyInformation.getN());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            return Algorithm.RSA512(rsaPublicKey, null);
        } catch (InvalidKeySpecException e) {
            logger.error("Could not parse key information from " + certificateEndpoint.toString());
            throw new RuntimeException(e);
        }
    }

    private boolean canProcessKey(KeyInformation keyInformation) {
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
