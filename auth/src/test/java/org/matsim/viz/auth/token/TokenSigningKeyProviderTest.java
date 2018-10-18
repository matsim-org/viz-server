package org.matsim.viz.auth.token;

import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;

import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.*;

public class TokenSigningKeyProviderTest {

    @Test
    public void constructor_instanceCreated() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        assertNotNull(provider);
        assertNotNull(provider.getPrivateKey());
    }

    @Test
    public void generateNewKey_newKeyIsGenerated() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        TokenSigningKeyProvider.RSAKeyPair keyPair = provider.generateNewKey();

        assertNotNull(keyPair);

        // test if id is set and uuid
        UUID id = UUID.fromString(keyPair.getId());
        assertNotNull(id);

        assertNotNull(keyPair.getPrivateKey());
        assertNotNull(keyPair.getPublicKey());
    }

    @Test
    public void generateKey_oldKeysShouldBeRemoved() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();
        TokenSigningKeyProvider.RSAKeyPair second = provider.generateNewKey();
        TokenSigningKeyProvider.RSAKeyPair third = provider.generateNewKey();

        PublicKey shouldBePresent = provider.getPublicKeyById(second.getId());
        PublicKey shouldalsoBePresent = provider.getPublicKeyById(third.getId());
        assertEquals(second.getPublicKey(), shouldBePresent);
        assertEquals(third.getPublicKey(), shouldalsoBePresent);

        try {
            PublicKey shouldCauseException = provider.getPublicKeyById(first.getId());
            fail("first keyPair should have been removed");
        } catch (Exception e) {
            // fine. first keyPair should have been removed an retreiving it should cause exception
        }
    }

    @Test
    public void getCurrentKey() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();
        TokenSigningKeyProvider.RSAKeyPair second = provider.generateNewKey();

        RSAPrivateKey result = provider.getPrivateKey();

        assertEquals(second.getPrivateKey(), result);
        assertNotSame(first.getPrivateKey(), result);
    }

    @Test
    public void getKeyInformation() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();
        TokenSigningKeyProvider.RSAKeyPair second = provider.generateNewKey();

        List<TokenSigningKeyProvider.KeyInformation> informations = provider.getKeyInformation();

        assertTrue(informations.stream().anyMatch(info -> first.getId().equals(info.getKid()) && info.getN() != null));
        assertTrue(informations.stream().anyMatch(info -> second.getId().equals(info.getKid()) && info.getN() != null));
    }

    @Test
    public void getKeyById() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();

        RSAPublicKey result = provider.getPublicKeyById(first.getId());

        assertEquals(first.getPublicKey(), result);

    }

    @Test(expected = InvalidInputException.class)
    public void getKeyById_notPresent_exception() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();

        provider.getPublicKeyById("invalid-id");

        fail("invalid keyPair id should cause exception");

    }
}
