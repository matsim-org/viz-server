package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import javax.servlet.http.HttpSession;
import java.net.URI;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class AuthorizationResourceTest {

    private AuthorizationResource testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
    }

    @Before
    public void setUp() {
        testObject = new AuthorizationResource();
        testObject.tokenService = mock(TokenService.class);
        testObject.authService = mock(AuthorizationService.class);
    }

    /*
    Tests for application flow
     */
    @Test(expected = InvalidInputException.class)
    public void doAuthorization_invalidRequest_exception() {

        AuthenticationRequest request = new AuthenticationGetRequest(
                "not-openid", "invalid-response-type", URI.create("http://uri.com"),
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");
        String token = "some-token";

        testObject.doAuthorization(request, session, token);

        fail("invalid auth request should cause exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void doAuthorization_invalidClient_exception() {

    }

    @Test
    public void doAuthorization_invalidToken_redirectToLogin() {

    }

    @Test
    public void doAuthorization_errorDuringProcessing_redirectToCallbackWithError() {

    }

    /*
    Tests for openid-connect spec
     */

    @Test
    public void b() {

    }
/*
    @Test(expected = InvalidInputException.class)
    public void handle_requestWithoutParamsNoSession_exception() {

        Request request = AuthorizationTestUtils.mockRequestWithQueryParamsMap(new HashMap<>());
        Response response = mock(Response.class);

        testObject.handle(request, response);

        fail("no parameters and no session should cause exception");
    }

    @Test
    public void handle_requestWithoutParamsWithSessionNoCookie_redirectToLogin() {

        Request request = AuthorizationTestUtils.mockRequestWithQueryParamsMap(new HashMap<>());
        Response response = mock(Response.class);
        AuthenticationRequest authRequest = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithParams().queryMap());
        AuthorizationRequestHandler.loginSession.put(request.session().id(), authRequest);
        when(testObject.tokenService.validateToken(any())).thenThrow(new UnauthorizedException("bla"));

        testObject.handle(request, response);

        verify(response).redirect(eq(Routes.LOGIN), eq(HttpStatus.FOUND));
    }

    @Test
    public void handle_requestWithoutParamsWithSessionWithCookie_redirect() {

        Request request = AuthorizationTestUtils.mockRequestWithQueryParamsMap(new HashMap<>());
        Response response = mock(Response.class);
        AuthenticationRequest authRequest = new AuthenticationRequest(AuthorizationTestUtils.mockRequestWithParams().queryMap());
        Token idToken = new Token();
        idToken.setSubjectId("any-id");
        AuthorizationRequestHandler.loginSession.put(request.session().id(), authRequest);
        when(testObject.tokenService.validateToken(any())).thenReturn(idToken);
        when(testObject.authService.generateAuthenticationResponse(any(), anyString())).thenReturn(authRequest.getRedirectUri());

        testObject.handle(request, response);

        verify(response).redirect(eq(authRequest.getRedirectUri().toString()), eq(HttpStatus.FOUND));
    }

    @Test(expected = InvalidInputException.class)
    public void handle_requestWithParamsWrongArgs_exception() {

        Request request = AuthorizationTestUtils.mockRequestWithParams("response_type", "wrong types");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        fail("invalid request should cause exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void handle_requestWithParamsInvalidClient_exception() {

        Request request = AuthorizationTestUtils.mockRequestWithParams();
        Response response = mock(Response.class);
        when(testObject.authService.validateClient(any())).thenThrow(new UnauthorizedException("bla"));

        testObject.handle(request, response);

        fail("invalid client should cause exception");
    }

    @Test
    public void handle_requestWithParamsNoCookie_redirectToLogin() {

        Request request = AuthorizationTestUtils.mockRequestWithParams();
        Response response = mock(Response.class);
        when(testObject.authService.validateClient(any())).thenReturn(null);
        when(testObject.tokenService.validateToken(any())).thenThrow(new UnauthorizedException("bla"));

        testObject.handle(request, response);

        verify(response).redirect(eq(Routes.LOGIN), eq(HttpStatus.FOUND));
    }

    @Test
    public void handle_requestWithParamsWithCookieProcessingError_redirectWithError() {

        Request request = AuthorizationTestUtils.mockRequestWithParams();
        Response response = mock(Response.class);
        Token idToken = new Token();
        idToken.setSubjectId("any-id");

        when(testObject.authService.validateClient(any())).thenReturn(null);
        when(testObject.tokenService.validateToken(any())).thenReturn(idToken);
        when(testObject.authService.generateAuthenticationResponse(any(), anyString())).thenThrow(new CodedException("some", "error"));

        testObject.handle(request, response);

        verify(response).redirect(anyString(), eq(HttpStatus.FOUND));
    }

    @Test
    public void handle_requestWithParamsWithCookie_redirect() {

        Request request = AuthorizationTestUtils.mockRequestWithParams();
        Response response = mock(Response.class);
        Token idToken = new Token();
        idToken.setSubjectId("any-id");

        when(testObject.authService.validateClient(any())).thenReturn(null);
        when(testObject.tokenService.validateToken(any())).thenReturn(idToken);
        when(testObject.authService.generateAuthenticationResponse(any(), anyString())).thenReturn(URI.create("http://some.uri"));

        testObject.handle(request, response);

        verify(response).redirect(anyString(), eq(HttpStatus.FOUND));
    }
    */
}
