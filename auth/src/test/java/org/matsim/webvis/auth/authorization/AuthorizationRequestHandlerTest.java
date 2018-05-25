package org.matsim.webvis.auth.authorization;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.Routes;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.RequestError;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
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
        testObject.authService = mock(AuthorizationService.class);
        when(testObject.authService.isValidClientInformation(any())).thenReturn(true);
        testObject.tokenService = mock(TokenService.class);
    }

    @Test
    public void handle_missingOrInvalidUri_errorResponse() {

        Request req = AuthorizationTestUtils.mockRequestWithParams(AuthenticationRequest.REDIRECT_URI, "invalid uri");
        Object result = testObject.handle(req, null);

        assertErrorResponse(result, RequestError.INVALID_REQUEST);
    }

    @Test
    public void handle_missingOrInvalidRequiredParameter_redirect() {

        Request req = AuthorizationTestUtils.mockRequestWithParams(AuthenticationRequest.SCOPE, "notopenid");
        Response res = mock(Response.class);
        final String expectedQuery = "error=invalid_request";

        Object result = testObject.handle(req, res);


        verify(res).redirect(contains(expectedQuery), eq(302));
    }

    @Test
    public void handle_invalidClientInformation_errorResponse() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.authService.isValidClientInformation(any())).thenReturn(false);

        Object result = testObject.handle(req, null);

        assertErrorResponse(result, RequestError.UNAUTHORIZED_CLIENT);
    }

    @Test
    public void handle_userIsNotLoggedIn_loginPrompt() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateToken(any())).thenThrow(new RuntimeException("message"));
        Response res = mock(Response.class);

        Object result = testObject.handle(req, res);

        verify(res).redirect(Routes.LOGIN, 302);
    }

    @Test
    public void handle_unknownUser_loginPrompt() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateToken(any())).thenThrow(new Exception("user error"));
        Response res = mock(Response.class);

        Object result = testObject.handle(req, res);

        verify(res).redirect(Routes.LOGIN, 302);
    }

    @Test
    public void handle_success_redirectWithParams() {

        User user = new User();
        URI uri = URI.create("http://resulting.uri");
        when(testObject.tokenService.validateToken(any())).thenReturn(new Token());
        when(testObject.authService.generateResponse(any(), any())).thenReturn(uri);

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        Response res = mock(Response.class);

        Object result = testObject.handle(req, res);

        assertTrue(result instanceof String);
        assertEquals("OK", (String) result);

        verify(res).redirect(eq(uri.toString()), eq(302));
    }

    private void assertErrorResponse(Object result, String errorCode) {
        assertTrue(result instanceof String);
        String[] message = ((String) result).split(" ");
        assertTrue(message.length > 1);
        assertEquals("error", message[0]);
        assertEquals(errorCode, message[1]);
    }
}
