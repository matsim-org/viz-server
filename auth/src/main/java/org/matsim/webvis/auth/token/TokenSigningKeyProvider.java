package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
class TokenSigningKeyProvider {

    private static Logger logger = LogManager.getLogger();

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    TokenSigningKeyProvider() throws Exception {
        this(Configuration.getInstance().getTokenSigningKeyStore());
    }

    //for unit testing
    TokenSigningKeyProvider(String keyStorePath) throws Exception {
        try {
            KeyStore store = loadKeyStore(keyStorePath);
            publicKey = loadPublicKey(store);
            privateKey = loadPrivateKey(store);
        } catch (Exception e) {
            logger.error("Failed to load signing keys from keystore.", e);
            if (Configuration.getInstance().isDebug()) {
                generateDebugKeys();
            } else {
                throw new Exception(e);
            }
        }
    }

    private KeyStore loadKeyStore(String keyStorePath) throws Exception {

        File keyStoreFile = new File(keyStorePath);
        try (FileInputStream stream = new FileInputStream(keyStoreFile)) {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(stream, Configuration.getInstance().getTokenSigningKeyStorePassword().toCharArray());
            return store;
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    private RSAPublicKey loadPublicKey(KeyStore store) throws Exception {

        Certificate cert = store.getCertificate(Configuration.getInstance().getTokenSigningKeyAlias());
        PublicKey publicKey = cert.getPublicKey();

        if (publicKey.getAlgorithm().equals("RSA")) {
            return (RSAPublicKey) publicKey;
        } else {
            throw new Exception("Public signing key is not an RSA key.");
        }
    }

    private RSAPrivateKey loadPrivateKey(KeyStore store) throws Exception {

        Key key = store.getKey(Configuration.getInstance().getTokenSigningKeyAlias(),
                Configuration.getInstance().getTokenSigningKeyStorePassword().toCharArray());
        try {
            return (RSAPrivateKey) key;
        } catch (ClassCastException e) {
            throw new Exception("Private signing key is not an RSA key.");
        }
    }

    private void generateDebugKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = (RSAPrivateKey) pair.getPrivate();
            publicKey = (RSAPublicKey) pair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
