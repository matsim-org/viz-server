package org.matsim.viz.clientAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

public class TestUtils {

    private static ObjectMapper mapper = new ObjectMapper();
    private static KeyPairGenerator generator;

    static {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static void mockCertificatesEndpoint(WireMockRule wireMock, PublicKeyProvider.KeyInformation[] info) throws JsonProcessingException {
        wireMock.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(info))
                ));
    }

    private static String encodeToString(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    static PublicKeyProvider.KeyInformation createKeyInformation(String keyId) {
        return createKeyInformation(keyId, generator.generateKeyPair().getPublic());
    }

    static PublicKeyProvider.KeyInformation createKeyInformation(String keyId, PublicKey key) {

        return new PublicKeyProvider.KeyInformation(
                keyId, encodeToString(key), "RS512", "sig", "RSA"
        );
    }

    static KeyPair createRSAKeyPair() {
        return generator.generateKeyPair();
    }
}
