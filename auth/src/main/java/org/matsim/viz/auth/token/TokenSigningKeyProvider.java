package org.matsim.viz.auth.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.matsim.viz.error.InvalidInputException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenSigningKeyProvider {

    private static final int maxNumberOfValidKeys = 2;
    private static final String algorithmType = "RSA";
    private static final int keysize = 2048;
    private static final String algorithmName = "RS512";
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

    RSAKeyPair generateNewKey() {

        KeyPair keyPair = generator.generateKeyPair();

        String keyId = UUID.randomUUID().toString();
        RSAKeyPair result = new RSAKeyPair(keyId, (RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
        keys.addFirst(result);
        this.removeOldKeys();
        return result;
    }

    private void removeOldKeys() {
        while (keys.size() > maxNumberOfValidKeys) {
            keys.removeLast();
        }
    }

    RSAKeyPair getCurrentKey() {
        return keys.getFirst();
    }

    RSAKeyPair getKeyById(String id) {
        return keys.stream().filter(key -> key.id.equals(id))
                .findFirst().orElseThrow(() -> new InvalidInputException("unknown key id"));
    }

    List<KeyInformation> getKeyInformation() {
        return keys.stream()
                .map(key -> new KeyInformation(key.id, Base64.getEncoder().encodeToString(key.getPublicKey().getEncoded())))
                .collect(Collectors.toList());
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
