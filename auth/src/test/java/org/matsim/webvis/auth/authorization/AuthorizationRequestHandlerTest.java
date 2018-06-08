package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.Routes;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthorizationRequestHandlerTest {

    private AuthorizationRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new AuthorizationRequestHandler();
        testObject.tokenService = mock(TokenService.class);
        testObject.authService = mock(AuthorizationService.class);
    }

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
}
