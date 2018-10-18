package org.matsim.viz.clientAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.auth.AuthenticationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OAuthIntrospectionAuthenticatorTest {

    private static final String clientPrincipal = "principal";
    private static final String clientSecret = "secret";
    private static Client jerseyClient = ClientBuilder.newClient();
    private static ObjectMapper mapper = new ObjectMapper();
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private URI idProvider;
    private OAuthIntrospectionAuthenticator<TestPrincipal> testObject;

    @BeforeClass
    public static void setUpFixture() {
        HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basicBuilder().build();
        jerseyClient.register(basicAuth);
    }

    @Before
    public void setUp() {
        idProvider = URI.create("http://localhost:" + wireMockRule.port());
        testObject = new OAuthIntrospectionAuthenticator<>(
                jerseyClient, idProvider, result -> Optional.of(new TestPrincipal(result.getSubjectId())),
                new Credentials(clientPrincipal, clientSecret)
        );
    }

    @Test
    public void authenticate_introspectionResultInactive_emptyOptional() throws JsonProcessingException, AuthenticationException {

        IntrospectionResult introspectionResult = new IntrospectionResult(false, "scope", "subject");
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(introspectionResult))
                ));
        final String token = "some-token";

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertFalse(result.isPresent());
        BasicCredentials expectedAuth = new BasicCredentials(clientPrincipal, clientSecret);
        verify(getRequestedFor(urlPathEqualTo("/introspect"))
                .withBasicAuth(expectedAuth)
                .withQueryParam("token", equalTo(token))
        );
    }

    @Test
    public void authenticate_introspectionResultActive_principal() throws JsonProcessingException, AuthenticationException {

        final String scope = "scope";
        final String subject = "subject";
        IntrospectionResult introspectionResult = new IntrospectionResult(true, scope, subject);
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(introspectionResult))
                ));
        final String token = "some-token";

        Optional<TestPrincipal> result = testObject.authenticate(token);

        assertTrue(result.isPresent());
        BasicCredentials expectedAuth = new BasicCredentials(clientPrincipal, clientSecret);
        verify(getRequestedFor(urlPathEqualTo("/introspect"))
                .withBasicAuth(expectedAuth)
                .withQueryParam("token", equalTo(token))
        );
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    private static class TestPrincipal implements Principal {

        private String name;
    }
}
