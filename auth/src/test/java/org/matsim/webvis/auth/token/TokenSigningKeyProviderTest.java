package org.matsim.webvis.auth.token;

public class TokenSigningKeyProviderTest {


   /* @Test(expected = Exception.class)
    public void initialization_noKeyStoreFile_exception() throws UnsupportedEncodingException, FileNotFoundException {

        TestUtils.loadEmptyTestConfig();
        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();

        fail("if loading RSA-Keys from KeyStore fails and the application is not in debug mode an exception is expected.");
    }

    @Test
    public void initialization_allPresent_rsaKeys() throws UnsupportedEncodingException, FileNotFoundException {

        TestUtils.loadTestConfig();

        TokenSigningKeyProvider provider = new TokenSigningKeyProvider(Configuration.getInstance().getTokenSigningKeyStore());

        assertNotNull(provider.getPrivateKey());
        assertNotNull(provider.getPublicKey());
    }

*/
}
