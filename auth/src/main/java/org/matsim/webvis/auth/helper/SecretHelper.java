package org.matsim.webvis.auth.helper;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class SecretHelper {


    private static final String algorithm = "PBKDF2WithHmacSHA512";
    private static final int iterations = 1024;
    private static final int keyLength = 256;
    private static final SecureRandom random = new SecureRandom();
    private static SecretKeyFactory keyFactory;

    public static boolean match(String secret, String compare) {
        return secret.equals(compare);
    }

    public static String getEncodedSecret(char[] secret, byte[] salt) {
        try {
            if (keyFactory == null) {
                keyFactory = SecretKeyFactory.getInstance(algorithm);
            }
            PBEKeySpec spec = new PBEKeySpec(secret, salt, iterations, keyLength);
            SecretKey key = keyFactory.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Base64.getEncoder().encodeToString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            //TODO logger.error("Error while hashing password.", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static byte[] getRandomSalt() {
        byte[] result = new byte[32];
        random.nextBytes(result);
        return result;
    }
}
