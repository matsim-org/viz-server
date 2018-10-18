package org.matsim.viz.clientAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.error.InternalException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class PublicKeyProviderTest {

    private static Client jerseyClient = ClientBuilder.newClient();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private URI idProvider;

    @BeforeClass
    public static void setUpFixture() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
    }

    @Before
    public void setUp() {
        idProvider = URI.create("http://localhost:" + wireMockRule.port());
    }

    @Test
    public void constructor_initializeWithoutException() {

        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        assertNotNull(keyProvider);
    }

    @Test(expected = InternalException.class)
    public void getPrivateKey_exception() {

        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        keyProvider.getPrivateKey();

        fail("get private key should cause exception");
    }

    @Test(expected = InternalException.class)
    public void getPrivateKeyId_exception() {

        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        keyProvider.getPrivateKeyId();

        fail("get private key should cause exception");
    }

    @Test
    public void getPublicKeyById_keyMustBeFetched_fetchedKey() throws JsonProcessingException {

        final String keyId = "my-key-id";
        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        TestUtils.mockCertificatesEndpoint(wireMockRule, new PublicKeyProvider.KeyInformation[]{TestUtils.createKeyInformation(keyId)});

        RSAPublicKey publicKey = keyProvider.getPublicKeyById(keyId);

        assertNotNull(publicKey);
        verify(getRequestedFor(urlEqualTo("/certificates")));
    }

    @Test
    public void getPublicKeyById_keyMustBeFetchedButKeyIdIsNotInResults_null() throws JsonProcessingException {

        final String keyId = "my-key-id";
        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        TestUtils.mockCertificatesEndpoint(wireMockRule, new PublicKeyProvider.KeyInformation[]{TestUtils.createKeyInformation("other-key-id-from-id-provider")});

        RSAPublicKey publicKey = keyProvider.getPublicKeyById(keyId);

        assertNull(publicKey);
        verify(getRequestedFor(urlEqualTo("/certificates")));
    }

    @Test
    public void getPublicKeyId_keyIsPresent_noFetching() throws JsonProcessingException {

        final String keyId = "my-key-id";
        PublicKeyProvider keyProvider = new PublicKeyProvider(jerseyClient, idProvider);
        TestUtils.mockCertificatesEndpoint(wireMockRule, new PublicKeyProvider.KeyInformation[]{TestUtils.createKeyInformation(keyId)});

        // first the key is not present and must be fetched
        RSAPublicKey publicKey = keyProvider.getPublicKeyById(keyId);
        assertNotNull(publicKey);

        RSAPublicKey publicKeySecond = keyProvider.getPublicKeyById(keyId);
        assertNotNull(publicKeySecond);

        verify(1, getRequestedFor(urlEqualTo("/certificates")));
    }
}
