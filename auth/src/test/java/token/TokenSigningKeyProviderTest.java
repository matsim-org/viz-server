package token;

import config.Configuration;
import org.junit.Test;
import util.TestUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TokenSigningKeyProviderTest {

    @Test
    public void initialization_noKeyStoreFile_debugKeys() throws Exception {

        TestUtils.loadEmptyTestConfig();

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        assertNotNull(provider.getPrivateKey());
        assertNotNull(provider.getPublicKey());
    }

    @Test(expected = RuntimeException.class)
    public void initialization_noKeyStoreFile_exception() throws Exception {

        Configuration.loadConfigFile(TestUtils.getEmptyTestConfigPath(), false);
        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        fail("if loading RSA-Keys from KeyStore fails and the application is not in debug mode an exception is expected.");
    }

    @Test
    public void initialization_allPresent_rsaKeys() throws Exception {

        Configuration.loadConfigFile(TestUtils.getTestConfigPath(), false);
        String keyStorePath = TestUtils.getResourcePath(Configuration.getInstance().getTokenSigningKeyStore());

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider(keyStorePath);

        assertNotNull(provider.getPrivateKey());
        assertNotNull(provider.getPublicKey());
    }
}
