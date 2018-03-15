package authorization;

import data.entities.*;
import org.junit.Before;
import org.junit.Test;
import relyingParty.RelyingPartyService;
import token.TokenService;

import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    private AuthorizationService testObject;

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

        URI result = testObject.generateResponse(request, null);

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

        URI result = testObject.generateResponse(request, null);

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

        URI result = testObject.generateResponse(request, null);

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
        accessToken.setToken("some-access-token");
        when(testObject.tokenService.grantAccess(any())).thenReturn(accessToken);

        URI redirectUri = URI.create("http://expected.uri");

        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getState()).thenReturn("");
        when(request.getType()).thenReturn(AuthenticationRequest.Type.AccessAndIdToken);
        when(request.getRedirectUri()).thenReturn(redirectUri);

        URI result = testObject.generateResponse(request, null);

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

        URI result = testObject.generateResponse(request, null);

        URI expected = URI.create(redirectUri.toString() + "#token_type=bearer&id_token=" +
                                          idToken.getToken() + "&state=some+state");
        assertEquals(expected, result);
    }
}
