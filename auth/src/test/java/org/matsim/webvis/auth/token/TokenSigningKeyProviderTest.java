package org.matsim.webvis.auth.token;

import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;

import static org.junit.Assert.assertNotNull;

public class TokenSigningKeyProviderTest {

    @Test
    public void initialization_allPresent_rsaKeys() {

        TestUtils.loadTestConfigIfNecessary();

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        assertNotNull(provider.getPrivateKey());
        assertNotNull(provider.getPublicKey());
    }
}
