package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InternalException;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    private AuthorizationService testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
    }

    @Before
    public void setUp() {
        testObject = AuthorizationService.Instance;
        testObject.userService = mock(UserService.class);
        testObject.relyingPartyService = mock(RelyingPartyService.class);
        testObject.tokenService = mock(TokenService.class);
    }

    @Test(expected = CodedException.class)
    public void isValidClient_clientInvalid_invalid() {

        when(testObject.relyingPartyService.validateClient(any(), any(), any())).thenThrow(new CodedException("bla", "bla"));

        testObject.validateClient(createAuthRequest());

        fail("exception of rpService should be passed through method");
    }

    @Test
    public void isValidClient_clientValid_client() {

        Client client = new Client();
        when(testObject.relyingPartyService.validateClient(any(), any(), any())).thenReturn(client);

        Client result = testObject.validateClient(createAuthRequest());

        assertEquals(client, result);

    }

    @Test(expected = InternalException.class)
    public void generateAuthenticationResponse_userNotFound_exception() {

        when(testObject.userService.findUser(anyString())).thenReturn(null);

        testObject.generateAuthenticationResponse(createAuthRequest(), "invalid-id");

        fail("missing user should cause exception");
    }

    @Test
    public void generateAuthenticationResponse_noState_uri() {

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");
        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
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

        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);

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

        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createIdToken(any(), anyString())).thenReturn(idToken);

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

        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
        when(testObject.tokenService.createIdToken(any(), anyString())).thenReturn(idToken);

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test(expected = InternalException.class)
    public void generateAuthenticationResponse_userNotFound_internalException() {

        AuthenticationRequest request = createAuthRequest();
        when(testObject.userService.findUser(anyString())).thenReturn(null);

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
