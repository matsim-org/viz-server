package org.matsim.viz.auth.authorization;

import org.junit.Test;
import org.matsim.viz.auth.entities.Client;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.relyingParty.RelyingPartyService;
import org.matsim.viz.auth.token.TokenService;
import org.matsim.viz.auth.user.UserService;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.InternalException;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    @Test(expected = CodedException.class)
    public void isValidClient_clientInvalid_invalid() {

        RelyingPartyService relyingPartyService = mock(RelyingPartyService.class);
        when(relyingPartyService.validateClient(any(), any(), any())).thenThrow(new CodedException(1, "bla", "bla"));
        AuthorizationService testObject = new AuthorizationService(mock(TokenService.class), mock(UserService.class), relyingPartyService);

        testObject.validateClient(createAuthRequest());

        fail("exception of rpService should be passed through method");
    }

    @Test
    public void isValidClient_clientValid_client() {

        Client client = new Client();
        RelyingPartyService relyingPartyService = mock(RelyingPartyService.class);
        when(relyingPartyService.validateClient(any(), any(), any())).thenReturn(client);
        AuthorizationService testObject = new AuthorizationService(mock(TokenService.class), mock(UserService.class), relyingPartyService);

        Client result = testObject.validateClient(createAuthRequest());

        assertEquals(client, result);
    }

    @Test(expected = InternalException.class)
    public void generateAuthenticationResponse_userNotFound_exception() {

        UserService userService = mock(UserService.class);
        when(userService.findUser(anyString())).thenReturn(null);
        AuthorizationService testObject = new AuthorizationService(mock(TokenService.class), userService, mock(RelyingPartyService.class));

        testObject.generateAuthenticationResponse(createAuthRequest(), "invalid-id");

        fail("missing user should cause exception");
    }

    @Test
    public void generateAuthenticationResponse_noState_uri() {

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");

        UserService userService = mock(UserService.class);
        TokenService tokenService = mock(TokenService.class);
        when(userService.findUser(anyString())).thenReturn(user);
        when(tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
        AuthorizationService testObject = new AuthorizationService(tokenService, userService, mock(RelyingPartyService.class));

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", URI.create("http://some.uri"),
                "client-id", "", "nonce"
        );
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer&access_token="
                + accessToken.getTokenValue()
        );

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test
    public void generateAuthenticationResponse_withState_uri() {

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");
        AuthenticationRequest request = createAuthRequest();
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + request.getState()
                + "&access_token=" + accessToken.getTokenValue()
        );

        UserService userService = mock(UserService.class);
        TokenService tokenService = mock(TokenService.class);
        when(userService.findUser(anyString())).thenReturn(user);
        when(tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
        AuthorizationService testObject = new AuthorizationService(tokenService, userService, mock(RelyingPartyService.class));

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test
    public void generateAuthenticationResponse_withIdToken_uri() {

        User user = new User();
        user.setId("some-id");
        Token idToken = new Token();
        idToken.setTokenValue("token-value");

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + request.getState()
                + "&id_token=" + idToken.getTokenValue()
        );

        UserService userService = mock(UserService.class);
        TokenService tokenService = mock(TokenService.class);
        when(userService.findUser(anyString())).thenReturn(user);
        when(tokenService.createIdToken(any(), anyString())).thenReturn(idToken);
        AuthorizationService testObject = new AuthorizationService(tokenService, userService, mock(RelyingPartyService.class));

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test
    public void generateAuthenticationResponse_withAccessTokenAndIdToken_uri() {

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");
        Token idToken = new Token();
        idToken.setTokenValue("some-other-value");

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + request.getState()
                + "&id_token=" + idToken.getTokenValue()
                + "&access_token=" + accessToken.getTokenValue()
        );

        UserService userService = mock(UserService.class);
        TokenService tokenService = mock(TokenService.class);
        when(userService.findUser(anyString())).thenReturn(user);
        when(tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
        when(tokenService.createIdToken(any(), anyString())).thenReturn(idToken);
        AuthorizationService testObject = new AuthorizationService(tokenService, userService, mock(RelyingPartyService.class));

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test(expected = InternalException.class)
    public void generateAuthenticationResponse_userNotFound_internalException() {

        AuthenticationRequest request = createAuthRequest();

        UserService userService = mock(UserService.class);
        when(userService.findUser(anyString())).thenReturn(null);
        AuthorizationService testObject = new AuthorizationService(mock(TokenService.class), userService, mock(RelyingPartyService.class));

        testObject.generateAuthenticationResponse(request, "any-id");

        fail("User not found should cause exception");
    }

    private AuthenticationRequest createAuthRequest() {
        return new AuthenticationPostRequest(
                "openid", "token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );
    }
}
