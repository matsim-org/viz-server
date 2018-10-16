package org.matsim.viz.auth.token;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TokenSigningKeyProvider {

    private static Logger logger = LoggerFactory.getLogger(TokenSigningKeyProvider.class);

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public TokenSigningKeyProvider(String keyStorePath, String keyAlias, String keyStorePassword) {
        KeyStore store = loadKeyStore(keyStorePath, keyStorePassword);
        publicKey = loadPublicKey(store, keyAlias);
        privateKey = loadPrivateKey(store, keyAlias, keyStorePassword);

    }

    private KeyStore loadKeyStore(String keyStorePath, String keyStorePassword) {

        File keyStoreFile = new File(keyStorePath);
        try (FileInputStream stream = new FileInputStream(keyStoreFile)) {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(stream, keyStorePassword.toCharArray());
            return store;
        } catch (Exception e) {
            logger.error("Failed to load keystore!", e);
            throw new RuntimeException(e);
        }
    }

    private RSAPublicKey loadPublicKey(KeyStore store, String keyAlias) {

        try {
            Certificate cert = store.getCertificate(keyAlias);
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

    private RSAPrivateKey loadPrivateKey(KeyStore store, String keyAlias, String keyStorePassword) {
        try {
            Key key = store.getKey(keyAlias, keyStorePassword.toCharArray());
            return (RSAPrivateKey) key;
        } catch (ClassCastException e) {
            throw new RuntimeException("Private signing key is not an RSA key.");
        } catch (Exception e) {
            logger.error("failed to load private token signing key", e);
            throw new RuntimeException(e);
        }
    }
}
