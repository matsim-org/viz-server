package org.matsim.viz.filesApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.clientAuth.ClientAuthentication;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class FilesApiTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String rpId = "rp-id";
    private static final String rpSecret = "secret";
    private static final String clientScope = "client-scope";
    private static final String token = "some-token";

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule();
    @Rule
    public WireMockClassRule wireMockRule = wireMockClassRule;

    private ClientAuthentication authentication;
    private URI filesEndpoint;
    private Client jerseyClient = ClientBuilder.newClient();

    @Before
    public void setUp() {
        jerseyClient = ClientBuilder.newClient();
        jerseyClient.register(HttpAuthenticationFeature.basic(rpId, rpSecret));
        jerseyClient.register(OAuth2ClientSupport.feature(null));
        filesEndpoint = URI.create("http://localhost:" + wireMockRule.port());
        authentication = mock(ClientAuthentication.class);
    }

    @Test
    public void fetchVisualization_noTokenPresent() throws JsonProcessingException {

        // if no token is presented files server will return 401
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withStatus(401))
        );

      /*  final String invalidToken = "invalid-token";
        // if invalid token is presented files server will return 401
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .withHeader("Authorization", equalTo(invalidToken))
                .willReturn(aResponse().withStatus(401))
        );
*/

        // expected visualizations
        String json = mapper.writeValueAsString(new Visualization[]{new Visualization()});

        // if auth token is presented expected visualizations are returned by files server
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
                ));

        // authentication is mocked. On first request there is no access token present
        // on second attempt to request from files server a token is provided
        when(authentication.getAccessToken()).thenReturn("invalid-token").thenReturn(token);

        final String vizType = "my-type";
        final Instant createdAfter = Instant.now();
        FilesApi api = new FilesApi(jerseyClient, authentication, filesEndpoint);
        Visualization[] result = api.fetchVisualizations(vizType, createdAfter);

        assertEquals(1, result.length);

        // there should be 2 attempts to request a new access token
        // once if token is not yet set
        // once if request was rejected due to invalid token
        verify(authentication, times(2)).requestAccessToken();

        // since the original request to files api is repeated after an access token was requested
        // getAccessToken and the call to /visualizations should be executed twice
        verify(authentication, times(2)).getAccessToken();
        WireMock.verify(2, getRequestedFor(urlPathEqualTo("/visualizations"))
                .withQueryParam("type", equalTo(vizType))
                .withQueryParam("after", equalTo(createdAfter.toString()))
        );
    }

    @Test
    public void fetchVisualization_tokenIsAlreadyPresent() throws JsonProcessingException {

        // if no token is presented files server will return 401
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withStatus(401))
        );

        // expected visualizations
        String json = mapper.writeValueAsString(new Visualization[]{new Visualization()});

        // if auth token is presented expected visualizations are returned by files server
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
                ));

        // authentication is mocked. Return access token each time it is requested
        when(authentication.getAccessToken()).thenReturn(token);
        when(authentication.hasAccessToken()).thenReturn(true);

        final String vizType = "my-type";
        final Instant createdAfter = Instant.now();
        FilesApi api = new FilesApi(jerseyClient, authentication, filesEndpoint);
        Visualization[] result = api.fetchVisualizations(vizType, createdAfter);

        assertEquals(1, result.length);

        // since token is already present no token should be requested
        verify(authentication, times(0)).requestAccessToken();

        // everything should work on the first try
        verify(authentication, times(1)).getAccessToken();
        WireMock.verify(1, getRequestedFor(urlPathEqualTo("/visualizations"))
                .withQueryParam("type", equalTo(vizType))
                .withQueryParam("after", equalTo(createdAfter.toString()))
        );
    }

    @Test(expected = InternalServerErrorException.class)
    public void fetchVisualization_errorWhileFetching() {

        // there might be an internal server error
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .willReturn(aResponse()
                        .withStatus(500))
        );

        when(authentication.getAccessToken()).thenReturn(token);

        final String vizType = "my-type";
        final Instant createdAfter = Instant.now();
        FilesApi api = new FilesApi(jerseyClient, authentication, filesEndpoint);
        api.fetchVisualizations(vizType, createdAfter);

        fail("should cause exception");
    }

    @Test
    public void downloadFile() throws IOException {

        byte[] file = new byte[1000];
        // if auth token is presented expected visualizations are returned by files server
        wireMockRule.stubFor(get(WireMock.anyUrl())
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/octet-stream")
                        .withBody(file)
                ));

        // authentication is mocked. Return access token each time it is requested
        when(authentication.getAccessToken()).thenReturn(token);

        final String projectId = "project-id";
        final String fileId = "file-id";
        FilesApi api = new FilesApi(jerseyClient, authentication, filesEndpoint);
        InputStream result = api.downloadFile(projectId, fileId);

        byte[] byteResult = new byte[1000];
        result.read(byteResult);
        assertEquals(file.length, byteResult.length);

        WireMock.verify(getRequestedFor(urlEqualTo("/projects/" + projectId + "/files/" + fileId)));
    }
}
