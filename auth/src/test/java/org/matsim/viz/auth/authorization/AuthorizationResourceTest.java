package org.matsim.viz.auth.authorization;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.matsim.viz.auth.entities.Client;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.token.TokenService;
import org.matsim.viz.auth.util.TestUtils;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.error.UnauthorizedException;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthorizationResourceTest {

    @Test(expected = InvalidInputException.class)
    public void doAuthorization_invalidRequest_exception() {

        AuthenticationRequest request = new AuthenticationGetRequest(
                "not-openid", "invalid-response-type", URI.create("http://uri.com"),
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");
        String token = "some-token";

        AuthorizationResource testObject = new AuthorizationResource(mock(TokenService.class), mock(AuthorizationService.class));

        testObject.doAuthorization(request, session, token);

        fail("invalid auth request should cause exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void doAuthorization_invalidClient_exception() {

        AuthorizationService authService = mock(AuthorizationService.class);
        when(authService.validateClient(any())).thenThrow(new UnauthorizedException("bla"));
        AuthorizationResource testObject = new AuthorizationResource(mock(TokenService.class), authService);

        AuthenticationRequest request = new AuthenticationGetRequest(
                "openid", "token", URI.create("http://uri.com"),
                "invalid-client", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");
        String token = "token";

        testObject.doAuthorization(request, session, token);

        fail("invalid client should cause exception");
    }

    @Test
    public void doAuthorization_invalidToken_redirectToLogin() {

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(anyString())).thenThrow(new UnauthorizedException("no"));
        when(authService.validateClient(any())).thenReturn(new Client());
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", URI.create("http://uri.com"),
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");
        String token = "token";

        Response response = testObject.doAuthorization(request, session, token);

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals("/login", response.getLocation().toString());
    }

    @Test
    public void doAuthorization_errorDuringProcessing_redirectToCallbackWithError() {

        Token token = new Token();
        token.setSubjectId("some-id");
        token.setTokenValue("some-value");
        URI callback = URI.create("http://some.callback");
        String errorCode = "code";
        String errorMessage = "message";

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(anyString())).thenReturn(token);
        when(authService.validateClient(any())).thenReturn(new Client());
        when(authService.generateAuthenticationResponse(any(), anyString())).thenThrow(new CodedException(1, errorCode, errorMessage));
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", callback,
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");

        Response response = testObject.doAuthorization(request, session, token.getTokenValue());

        assertEquals(Response.Status.Family.SERVER_ERROR, response.getStatusInfo().getFamily());
        assertEquals(callback.getHost(), response.getLocation().getHost());
        assertEquals("error=" + errorCode + "&error_description=" + errorMessage, response.getLocation().getQuery());
    }

    @Test
    public void doAuthorization_validRequest_redirectToCallback() {

        Token token = new Token();
        token.setSubjectId("some-id");
        token.setTokenValue("some-value");
        URI callback = URI.create("http://some.callback");

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(token.getTokenValue())).thenReturn(token);
        when(authService.validateClient(any())).thenReturn(new Client());
        when(authService.generateAuthenticationResponse(any(), anyString())).thenReturn(callback);
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", callback,
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");

        Response response = testObject.doAuthorization(request, session, token.getTokenValue());

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals(callback.getHost(), response.getLocation().getHost());
        assertTrue(StringUtils.isBlank(response.getLocation().getQuery()));
    }

    @Test
    public void authorize_postRequest_redirectToCallback() {

        Token token = new Token();
        token.setSubjectId("some-id");
        token.setTokenValue("some-value");
        URI callback = URI.create("http://some.callback");

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(token.getTokenValue())).thenReturn(token);
        when(authService.validateClient(any())).thenReturn(new Client());
        when(authService.generateAuthenticationResponse(any(), anyString())).thenReturn(callback);
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        AuthenticationPostRequest request = new AuthenticationPostRequest(
                "openid", "token", callback,
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");

        Response response = testObject.authorize(request, session, token.getTokenValue());

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals(callback.getHost(), response.getLocation().getHost());
        assertTrue(StringUtils.isBlank(response.getLocation().getQuery()));
    }

    @Test
    public void authorize_getRequest_redirectToCallback() {

        Token token = new Token();
        token.setSubjectId("some-id");
        token.setTokenValue("some-value");
        URI callback = URI.create("http://some.callback");

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(token.getTokenValue())).thenReturn(token);
        when(authService.validateClient(any())).thenReturn(new Client());
        when(authService.generateAuthenticationResponse(any(), anyString())).thenReturn(callback);
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        AuthenticationGetRequest request = new AuthenticationGetRequest(
                "openid", "token", callback,
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");

        Response response = testObject.authorize(request, session, token.getTokenValue());

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals(callback.getHost(), response.getLocation().getHost());
        assertTrue(StringUtils.isBlank(response.getLocation().getQuery()));
    }

    @Test(expected = InvalidInputException.class)
    public void afterLogin_noRequest_exception() {

        AuthorizationResource testObject = new AuthorizationResource(mock(TokenService.class), mock(AuthorizationService.class));
        HttpSession session = TestUtils.mockSession("id");

        testObject.afterLogin(session, "some-token");

        fail("no stored session should cause exception");
    }

    @Test
    public void afterLogin_success_redirect() {

        Token token = new Token();
        token.setSubjectId("some-id");
        token.setTokenValue("some-value");
        URI callback = URI.create("http://some.callback");
        AuthenticationGetRequest request = new AuthenticationGetRequest(
                "openid", "token", callback,
                "client-id", "state", "nonce"
        );
        HttpSession session = TestUtils.mockSession("id");
        AuthorizationResource.loginSession.put(session.getId(), request);

        TokenService tokenService = mock(TokenService.class);
        AuthorizationService authService = mock(AuthorizationService.class);
        when(tokenService.validateToken(token.getTokenValue())).thenReturn(token);
        when(authService.validateClient(any())).thenReturn(new Client());
        when(authService.generateAuthenticationResponse(any(), anyString())).thenReturn(callback);
        AuthorizationResource testObject = new AuthorizationResource(tokenService, authService);

        Response response = testObject.afterLogin(session, token.getTokenValue());

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals(callback.getHost(), response.getLocation().getHost());
        assertTrue(StringUtils.isBlank(response.getLocation().getQuery()));

        verify(session).invalidate();
    }
}
