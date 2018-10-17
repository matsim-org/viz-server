package org.matsim.viz.clientAuth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class OAuthAuthenticatorTest {

    private static Client jerseyClient = ClientBuilder.newClient();
    private static ObjectMapper mapper = new ObjectMapper();
    private static KeyPairGenerator generator;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @BeforeClass
    public static void setUpFixture() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
    }

    private static OAuthAuthenticator.KeyInformation createKeyInformation(String keyId) {
        return new OAuthAuthenticator.KeyInformation(
                keyId, getEncodedPublicKey(), "RS512", "sig", "RSA"
        );
    }

    private static String getEncodedPublicKey() {
        KeyPair keyPair = generator.generateKeyPair();
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    @Test
    public void initialize_fetchKeysFromEndpoint() throws JsonProcessingException {

        // create mock server that returns keys
        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(createKeyInformation("my-key"));
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));

        URI idProvider = URI.create("http://localhost:" + wireMockRule.port());

        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal()));

        assertNotNull(authenticator);
        verify(getRequestedFor(urlEqualTo("/certificates")));
    }

    @Test
    public void initialize_cannotReadFetchedKey() throws JsonProcessingException {

        // create mock server that returns keys
        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(new OAuthAuthenticator.KeyInformation(
                "my-key", "invalid-rsa-key", "RS512", "sig", "RSA"
        ));

        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));

        URI idProvider = URI.create("http://localhost:" + wireMockRule.port());
        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal()));

        assertNotNull(authenticator);
        verify(getRequestedFor(urlEqualTo("/certificates")));
    }

    @Test
    public void initialize_unknownKeyFormat() throws JsonProcessingException {

        // create mock server that returns keys
        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(new OAuthAuthenticator.KeyInformation(
                "my-key", getEncodedPublicKey(), "RS512", "sig", "DSA"
        ));

        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));

        URI idProvider = URI.create("http://localhost:" + wireMockRule.port());
        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal()));

        assertNotNull(authenticator);
        verify(getRequestedFor(urlEqualTo("/certificates")));
    }

    @Test
    public void authenticate_malformedToken_emptyOptional() throws JsonProcessingException {

        final String token = "invalid-jwt";
        final URI idProvider = URI.create("http://localhost:" + wireMockRule.port());

        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(createKeyInformation("my-key"));
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));
        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal()));

        Optional<Principal> result = authenticator.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void authenticate_unknownKey_newKeysAreFetched() throws JsonProcessingException {

        final URI idProvider = URI.create("http://localhost:" + wireMockRule.port());
        final String keyId = "my-key";
        final String subject = "some-user-id";
        final KeyPair keyPair = generator.generateKeyPair();
        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(publicKey, privateKey));

        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(new OAuthAuthenticator.KeyInformation(
                "my-key", Base64.getEncoder().encodeToString(publicKey.getEncoded()), "RS512", "sig", "RSA"
        ));
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));

        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal(result.getSubjectId())));

        Optional<Principal> result = authenticator.authenticate(token);

        assertTrue(result.isPresent());
        assertEquals(subject, result.get().getName());
    }

    @Test
    public void authenticate_unknownKey_newKeysAreFetchedButKeyIsNotThere_emptyOptional() throws JsonProcessingException {

        final URI idProvider = URI.create("http://localhost:" + wireMockRule.port());
        final String keyId = "my-key";
        final String subject = "some-user-id";
        final KeyPair keyPair = generator.generateKeyPair();
        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        final String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withKeyId(keyId)
                .withIssuer(idProvider.toString())
                .sign(Algorithm.RSA512(publicKey, privateKey));

        List<OAuthAuthenticator.KeyInformation> keyInformations = new ArrayList<>();
        keyInformations.add(new OAuthAuthenticator.KeyInformation(
                "other-key-id", Base64.getEncoder().encodeToString(publicKey.getEncoded()), "RS512", "sig", "RSA"
        ));
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(keyInformations))
                ));

        OAuthAuthenticator<Principal> authenticator =
                new OAuthAuthenticator<>(jerseyClient, idProvider, result -> Optional.of(new Principal(result.getSubjectId())));

        Optional<Principal> result = authenticator.authenticate(token);

        assertFalse(result.isPresent());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Principal implements java.security.Principal {

        private String name;
    }
}
