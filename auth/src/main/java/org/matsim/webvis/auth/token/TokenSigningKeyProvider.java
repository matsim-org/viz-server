package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
class TokenSigningKeyProvider {

    private static Logger logger = LogManager.getLogger();

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    TokenSigningKeyProvider() {
        this(Configuration.getInstance().getTokenSigningKeyStore());
    }

    //for unit testing
    TokenSigningKeyProvider(String keyStorePath) {
            KeyStore store = loadKeyStore(keyStorePath);
            publicKey = loadPublicKey(store);
            privateKey = loadPrivateKey(store);

    }

    private KeyStore loadKeyStore(String keyStorePath) {

        File keyStoreFile = new File(keyStorePath);
        try (FileInputStream stream = new FileInputStream(keyStoreFile)) {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(stream, Configuration.getInstance().getTokenSigningKeyStorePassword().toCharArray());
            return store;
        } catch (Exception e) {
            logger.error("Failed to load keystore!", e);
            throw new RuntimeException(e);
        }
    }

    private RSAPublicKey loadPublicKey(KeyStore store) {

        try {
            Certificate cert = store.getCertificate(Configuration.getInstance().getTokenSigningKeyAlias());
            PublicKey publicKey = cert.getPublicKey();
            return (RSAPublicKey) publicKey;

        } catch (KeyStoreException e) {
            logger.error("Failed to load public token signing key.");
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            logger.error("public signing key was not an RSA key.");
            throw new RuntimeException(e);
        }
    }

    private RSAPrivateKey loadPrivateKey(KeyStore store) {
        try {
        Key key = store.getKey(Configuration.getInstance().getTokenSigningKeyAlias(),
                Configuration.getInstance().getTokenSigningKeyStorePassword().toCharArray());
            return (RSAPrivateKey) key;
        } catch (ClassCastException e) {
            throw new RuntimeException("Private signing key is not an RSA key.");
        } catch (Exception e) {
            logger.error("failed to load private token signing key", e);
            throw new RuntimeException(e);
        }
    }
}
