package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InternalException;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    private AuthorizationService testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
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
        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getScopes()).thenReturn(new String[0]);

        testObject.validateClient(request);

        fail("exception of rpService should be passed through method");
    }

    @Test
    public void isValidClient_clientValid_client() {

        Client client = new Client();
        when(testObject.relyingPartyService.validateClient(any(), any(), any())).thenReturn(client);
        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getScopes()).thenReturn(new String[0]);

        Client result = testObject.validateClient(request);

        assertEquals(client, result);

    }

    @Test(expected = InternalException.class)
    public void generateAuthenticationResponse_userNotFound_exception() {

        when(testObject.userService.findUser(anyString())).thenReturn(null);
        AuthenticationRequest request = mock(AuthenticationRequest.class);

        testObject.generateAuthenticationResponse(request, "invalid-id");

        fail("missing user should cause exception");
    }

    @Test
    public void generateAuthenticationResponse_noState_uri() {


    }

    @Test
    public void generateAuthenticationResponse_withState_uri() {

    }

    @Test
    public void generateAuthenticationResponse_withIdToken_uri() {

    }

    @Test
    public void generateAuthenticationResponse_withAccessToken_uri() {

    }

    @Test
    public void generateAuthenticationResponse_withAccessTokenAndIdToken_uri() {

    }

    /*
    @Test
    public void generateResponse_accessIdToken_uri() {

        Token idToken = new Token();
        idToken.setTokenValue("some-token");
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
                idToken.getTokenValue());
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_accessIdTokenToken_uri() {

        Token idToken = new Token();
        idToken.setTokenValue("some-token");
        when(testObject.tokenService.createIdToken(any(), any())).thenReturn(idToken);

        Token accessToken = new Token();
        accessToken.setTokenValue("some-token-value");
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
                idToken.getTokenValue() + "&access_token=" +
                accessToken.getTokenValue());
        assertEquals(expected, result);
    }

    @Test
    public void generateResponse_accessIdTokenWithState_uri() {

        Token idToken = new Token();
        idToken.setTokenValue("some-token");
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
                idToken.getTokenValue() + "&state=some+state");
        assertEquals(expected, result);
    }

    */
}
