package org.matsim.webvis.auth.helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class SecretHelperTest {

    @Test
    public void match_noMatch_false() {

        String secret = "secret";
        String toCompare = "not equal";

        boolean result = SecretHelper.match(secret, toCompare);

        assertFalse(result);
    }

    @Test
    public void match_doMatch_true() {

        String secret = "secret";
        String toCompare = "secret";

        boolean result = SecretHelper.match(secret, toCompare);

        assertTrue(result);
    }

    @Test
    public void getEncodedSecret_encodesString() {

        char[] secret = "some-secret".toCharArray();
        byte[] salt = SecretHelper.getRandomSalt();

        String result = SecretHelper.getEncodedSecret(secret, salt);

        assertNotNull(result);
    }
}
