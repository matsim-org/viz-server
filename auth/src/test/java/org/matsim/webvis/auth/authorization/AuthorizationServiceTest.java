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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

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
        when(request.getScope()).thenReturn("");

        testObject.validateClient(request);

        fail("exception of rpService should be passed through method");
    }

    @Test
    public void isValidClient_clientValid_client() {

        Client client = new Client();
        when(testObject.relyingPartyService.validateClient(any(), any(), any())).thenReturn(client);
        AuthenticationRequest request = mock(AuthenticationRequest.class);
        when(request.getScope()).thenReturn("");

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

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");
        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);
        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithParams().queryMap());
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
        String state = "some-state";
        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithParams("state", state).queryMap());
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + state
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
        String state = "some-state";

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put("response_type", new String[]{"id_token"});
        map.put("nonce", new String[]{"some-nonce"});
        map.put("state", new String[]{state});
        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithQueryParamsMap(map).queryMap());
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + state
                + "&id_token=" + idToken.getTokenValue()
        );

        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createIdToken(any(), anyString())).thenReturn(idToken);

        URI uri = testObject.generateAuthenticationResponse(request, "any-id");

        assertEquals(expectedURI, uri);
    }

    @Test
    public void generateAuthenticationResponse_withAccessToken_uri() {

        User user = new User();
        user.setId("some-id");
        Token accessToken = new Token();
        accessToken.setTokenValue("token-value");
        String state = "some-state";

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put("response_type", new String[]{"token"});
        map.put("nonce", new String[]{"some-nonce"});
        map.put("state", new String[]{state});
        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithQueryParamsMap(map).queryMap());
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + state
                + "&access_token=" + accessToken.getTokenValue()
        );

        when(testObject.userService.findUser(anyString())).thenReturn(user);
        when(testObject.tokenService.createAccessToken(any(), anyString())).thenReturn(accessToken);

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
        String state = "some-state";

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put("response_type", new String[]{"id_token token"});
        map.put("nonce", new String[]{"some-nonce"});
        map.put("state", new String[]{state});
        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithQueryParamsMap(map).queryMap());
        URI expectedURI = URI.create(request.getRedirectUri()
                + "#token_type=bearer"
                + "&state=" + state
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

        AuthenticationRequest request = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithParams().queryMap());
        when(testObject.userService.findUser(anyString())).thenReturn(null);

        testObject.generateAuthenticationResponse(request, "any-id");

        fail("User not found should cause exception");
    }
}
