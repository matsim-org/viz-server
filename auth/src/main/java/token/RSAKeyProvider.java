package token;

import config.Configuration;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

@Getter
public class RSAKeyProvider {

    private static Logger logger = LogManager.getLogger();

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSAKeyProvider(String keystorepath) throws Exception{

        try {
            KeyStore keystore = loadKeystore(keystorepath);
            publicKey = loadPublicKey(keystore);
            privateKey = loadPrivateKey(keystore);
        } catch (Exception e) {
            logger.error("Error loading key from keystore", e);
            throw e;
        }
    }

    private KeyStore loadKeystore(String keystorepath) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        File keystoreFile = new File(keystorepath);
        try (FileInputStream is = new FileInputStream(keystoreFile)) {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(is, Configuration.getInstance().getKeyStorePassword().toCharArray());
            return store;
        } catch (IOException e) {
            logger.error("failed to open keystore file.", e);
            throw e;
        }
    }

    private RSAPublicKey loadPublicKey(KeyStore store) throws KeyStoreException {
        Certificate cert = store.getCertificate("selfsigned");
        PublicKey publicKey = cert.getPublicKey();
        if (publicKey.getAlgorithm().equals("RSA"))
        {
            return (RSAPublicKey)publicKey;
        }
        return null;
    }

    private RSAPrivateKey loadPrivateKey(KeyStore store) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        PrivateKey key = (PrivateKey) store.getKey("selfsigned", Configuration.getInstance().getKeyStorePassword().toCharArray());
        if (key.getAlgorithm().equals("RSA")) {
            return (RSAPrivateKey)key;
        }
        return null;
    }
}
