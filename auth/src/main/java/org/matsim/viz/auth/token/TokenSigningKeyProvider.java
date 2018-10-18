package org.matsim.viz.auth.token;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.matsim.viz.error.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TokenSigningKeyProvider implements RSAKeyProvider {

    private static final int maxNumberOfValidKeys = 2;
    private static final String algorithmType = "RSA";
    private static final int keysize = 2048;
    private static final String algorithmName = "RS512";
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Logger logger = LoggerFactory.getLogger(TokenSigningKeyProvider.class);

    private final KeyPairGenerator generator;
    private LinkedList<RSAKeyPair> keys = new LinkedList<>();

    public TokenSigningKeyProvider() {
        try {
            generator = KeyPairGenerator.getInstance(algorithmType);
            generator.initialize(keysize);
            this.generateNewKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleKeyRenewal(int intervalInHours) {
        logger.info("scheduling key renewal for every " + intervalInHours + " hours");
        scheduler.scheduleAtFixedRate(this::generateNewKey, intervalInHours, intervalInHours, TimeUnit.HOURS);
    }

    RSAKeyPair generateNewKey() {

        KeyPair keyPair = generator.generateKeyPair();
        String keyId = UUID.randomUUID().toString();
        logger.info("Generating new RSA key with id " + keyId);
        RSAKeyPair result = new RSAKeyPair(keyId, (RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
        keys.addFirst(result);
        this.removeOldKeys();
        return result;
    }

    private void removeOldKeys() {
        while (keys.size() > maxNumberOfValidKeys) {
            logger.info("Removing oldest key");
            keys.removeLast();
        }
    }

    List<KeyInformation> getKeyInformation() {
        return keys.stream()
                .map(key -> new KeyInformation(key.id, Base64.getEncoder().encodeToString(key.getPublicKey().getEncoded())))
                .collect(Collectors.toList());
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return keys.stream().filter(key -> key.id.equals(keyId))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("unknown key id"))
                .getPublicKey();
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return keys.getFirst().getPrivateKey();
    }

    @Override
    public String getPrivateKeyId() {
        return keys.getFirst().getId();
    }

    @Getter
    @AllArgsConstructor
    static class RSAKeyPair {
        private String id;
        private RSAPrivateKey privateKey;
        private RSAPublicKey publicKey;
    }

    @RequiredArgsConstructor
    @Getter
    static class KeyInformation {

        private final String kid;
        private final String n;
        private final String alg = algorithmName;
        private final String use = "sig";
        private final String kty = algorithmType;
    }
}
