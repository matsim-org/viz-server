package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.*;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    private AuthorizationService testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() throws Exception {
        testObject = new AuthorizationService();
        testObject.relyingPartyService = mock(RelyingPartyService.class);
        testObject.tokenService = mock(TokenService.class);
    }

    @Test
    public void isValidClientInformation_clientNotRegistered_invalid() {

        when(testObject.relyingPartyService.findClient(any())).thenReturn(null);
        AuthenticationRequest request = mock(AuthenticationRequest.class);

        boolean result = testObject.isValidClientInformation(request);

        assertFalse(result);
    }

    @Test
    public void isValidClientInformation_redirectUriNotRegistered_invalid() {

        Client client = new Client();
        RedirectUri someUri = new RedirectUri();
        someUri.setUri("http://some.uri");
        client.getRedirectUris().add(someUri);
        URI otherUri = URI.create("http://some-other.uri");

        when(testObject.relyingPartyService.findClient(any())).thenReturn(client);
        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getRedirectUri()).thenReturn(otherUri);

        boolean result = testObject.isValidClientInformation(request);

        assertFalse(result);
    }

    @Test
    public void isValidClientInformation_validClientValidUri_valid() {

        URI redirectUri = URI.create("http://some.uri");
        Client client = new Client();
        RedirectUri someUri = new RedirectUri();
        someUri.setUri(redirectUri.toString());
        client.getRedirectUris().add(someUri);

        when(testObject.relyingPartyService.findClient(any())).thenReturn(client);
        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getRedirectUri()).thenReturn(redirectUri);

        boolean result = testObject.isValidClientInformation(request);

        assertTrue(result);
    }

    @Test
    public void generateResponse_authResponseWithState_uri() {

        AuthorizationCode authToken = new AuthorizationCode();
        authToken.setToken("some-token");

        when(testObject.tokenService.createAuthorizationCode(any(), any()))
                .thenReturn(authToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("some state");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.AuthCode);
        when(request.getRedirectUri()).thenReturn(redirectUri);

        User user = new User();
        user.setId("id");

        URI result = testObject.generateResponse(request, user);

        URI expected = URI.create(redirectUri.toString() + "?code=" + authToken.getToken() + "&state=some+state");
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_authResponseNoState_uri() {

        AuthorizationCode authToken = new AuthorizationCode();
        authToken.setToken("some-token");

        when(testObject.tokenService.createAuthorizationCode(any(), any()))
                .thenReturn(authToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.AuthCode);
        when(request.getRedirectUri()).thenReturn(redirectUri);
        User user = new User();
        user.setId("id");

        URI result = testObject.generateResponse(request, user);

        URI expected = URI.create(redirectUri.toString() + "?code=" + authToken.getToken());
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_accessIdToken_uri() {

        IdToken idToken = new IdToken();
        idToken.setToken("some-token");
        when(testObject.tokenService.createIdToken(any(), any())).thenReturn(idToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.IdToken);
        when(request.getRedirectUri()).thenReturn(redirectUri);
        User user = new User();
        user.setEMail("mail");

        URI result = testObject.generateResponse(request, user);

        URI expected = URI.create(redirectUri.toString() + "#token_type=bearer&id_token=" +
                                          idToken.getToken());
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_accessIdTokenToken_uri() {

        IdToken idToken = new IdToken();
        idToken.setToken("some-token");
        when(testObject.tokenService.createIdToken(any(), any())).thenReturn(idToken);

        AccessToken accessToken = new AccessToken();
        accessToken.setToken("some-token");
        when(testObject.tokenService.grantAccess(any())).thenReturn(accessToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.AccessAndIdToken);
        when(request.getRedirectUri()).thenReturn(redirectUri);
        User user = new User();
        user.setEMail("mail");

        URI result = testObject.generateResponse(request, user);

        URI expected = URI.create(redirectUri.toString() + "#token_type=bearer&id_token=" +
                                          idToken.getToken() + "&access_token=" +
                                          accessToken.getToken());
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_accessIdTokenWithState_uri() {

        IdToken idToken = new IdToken();
        idToken.setToken("some-token");
        when(testObject.tokenService.createIdToken(any(), any())).thenReturn(idToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("some state");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.IdToken);
        when(request.getRedirectUri()).thenReturn(redirectUri);
        User user = new User();
        user.setEMail("mail");

        URI result = testObject.generateResponse(request, user);

        URI expected = URI.create(redirectUri.toString() + "#token_type=bearer&id_token=" +
                                          idToken.getToken() + "&state=some+state");
        assertEquals(expected, result);
    }
}