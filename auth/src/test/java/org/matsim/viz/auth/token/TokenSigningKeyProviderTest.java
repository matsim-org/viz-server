package org.matsim.viz.auth.token;

import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.*;

public class TokenSigningKeyProviderTest {

    @Test
    public void constructor_instanceCreated() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        assertNotNull(provider);
        assertNotNull(provider.getCurrentKey());
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

        TokenSigningKeyProvider.RSAKeyPair shouldBePresent = provider.getKeyById(second.getId());
        TokenSigningKeyProvider.RSAKeyPair shouldalsoBePresent = provider.getKeyById(third.getId());
        assertEquals(second, shouldBePresent);
        assertEquals(third, shouldalsoBePresent);

        try {
            TokenSigningKeyProvider.RSAKeyPair shouldCauseException = provider.getKeyById(first.getId());
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

        TokenSigningKeyProvider.RSAKeyPair result = provider.getCurrentKey();

        assertEquals(second.getId(), result.getId());
        assertNotSame(first, result);
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

        TokenSigningKeyProvider.RSAKeyPair result = provider.getKeyById(first.getId());

        assertEquals(first.getId(), result.getId());

    }

    @Test(expected = InvalidInputException.class)
    public void getKeyById_notPresent_exception() {

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        TokenSigningKeyProvider.RSAKeyPair first = provider.generateNewKey();

        provider.getKeyById("invalid-id");

        fail("invalid keyPair id should cause exception");

    }
}
