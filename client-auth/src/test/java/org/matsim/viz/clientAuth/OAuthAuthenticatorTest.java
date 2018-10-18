package org.matsim.viz.clientAuth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static junit.framework.TestCase.*;

public class OAuthAuthenticatorTest {


    private static Client jerseyClient = ClientBuilder.newClient();

    // since authenticator and public key provider are closely entangled we are mocking the id-provider instead of
    // the key provider
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private URI idProvider;
    private OAuthAuthenticator<TestPrincipal> testObject;

    @BeforeClass
    public static void setUpFixture() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
    }

    @Before
    public void setUp() {
        idProvider = URI.create("http://localhost:" + wireMockRule.port());
        testObject = getAuthenticator();
    }

    @Test
    public void initialize() {

        OAuthAuthenticator<TestPrincipal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new TestPrincipal()));

        assertNotNull(authenticator);
    }

    @Test
    public void authenticate_malformedToken_emptyOptional() throws JsonProcessingException {

        final String keyId = "my-key-id";
        TestUtils.mockCertificatesEndpoint(wireMockRule, new PublicKeyProvider.KeyInformation[]{TestUtils.createKeyInformation(keyId)});
        final String malformedToken = "some-token";

        Optional<TestPrincipal> result = testObject.authenticate(malformedToken);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_expiredToken_emptyOptional() throws JsonProcessingException {

        final KeyPair keyPair = TestUtils.createRSAKeyPair();
        final String keyId = "my-key-id";
        final String subject = "some-subject-id";
        final PublicKeyProvider.KeyInformation[] keys = new PublicKeyProvider.KeyInformation[]{
                TestUtils.createKeyInformation(keyId, keyPair.getPublic())
        };
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(null, (RSAPrivateKey) keyPair.getPrivate()));
        TestUtils.mockCertificatesEndpoint(wireMockRule, keys);

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_unknownIssuerInToken_emptyOptional() throws JsonProcessingException {

        final KeyPair keyPair = TestUtils.createRSAKeyPair();
        final String keyId = "my-key-id";
        final String subject = "some-subject-id";
        final PublicKeyProvider.KeyInformation[] keys = new PublicKeyProvider.KeyInformation[]{
                TestUtils.createKeyInformation(keyId, keyPair.getPublic())
        };
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(URI.create("https://unknown-issuer.com").toString())
                .sign(Algorithm.RSA512(null, (RSAPrivateKey) keyPair.getPrivate()));
        TestUtils.mockCertificatesEndpoint(wireMockRule, keys);

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_cantFetchKeyId_emptyOptional() throws JsonProcessingException {

        final KeyPair keyPair = TestUtils.createRSAKeyPair();
        final String keyId = "my-key-id";
        final String subject = "some-subject-id";
        final PublicKeyProvider.KeyInformation[] keys = new PublicKeyProvider.KeyInformation[]{
                TestUtils.createKeyInformation(keyId, keyPair.getPublic())
        };
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId("some-other-key-id")
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(null, (RSAPrivateKey) keyPair.getPrivate()));
        TestUtils.mockCertificatesEndpoint(wireMockRule, keys);

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_principalProviderException_emptyOptional() throws JsonProcessingException {

        final KeyPair keyPair = TestUtils.createRSAKeyPair();
        final String keyId = "my-key-id";
        final String subject = "some-subject-id";
        final PublicKeyProvider.KeyInformation[] keys = new PublicKeyProvider.KeyInformation[]{
                TestUtils.createKeyInformation(keyId, keyPair.getPublic())
        };
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(null, (RSAPrivateKey) keyPair.getPrivate()));
        TestUtils.mockCertificatesEndpoint(wireMockRule, keys);

        testObject = new OAuthAuthenticator<>(jerseyClient, idProvider, result -> {
            throw new RuntimeException("something went wrong");
        });

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_allGood_principal() throws JsonProcessingException {

        final KeyPair keyPair = TestUtils.createRSAKeyPair();
        final String keyId = "my-key-id";
        final String subject = "some-subject-id";
        final PublicKeyProvider.KeyInformation[] keys = new PublicKeyProvider.KeyInformation[]{
                TestUtils.createKeyInformation(keyId, keyPair.getPublic())
        };
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(null, (RSAPrivateKey) keyPair.getPrivate()));
        TestUtils.mockCertificatesEndpoint(wireMockRule, keys);

        testObject = new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new TestPrincipal(result.getSubjectId())));

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertTrue(result.isPresent());
        assertEquals(subject, result.get().getName());
    }

    private OAuthAuthenticator<TestPrincipal> getAuthenticator() {
        return new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new TestPrincipal()));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    private static class TestPrincipal implements Principal {

        private String name;
    }
}
