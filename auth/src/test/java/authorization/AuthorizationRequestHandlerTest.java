package authorization;

import data.entities.User;
import org.junit.Before;
import org.junit.Test;
import requests.ErrorCode;
import spark.Request;
import spark.Response;
import token.TokenService;

import java.net.URI;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthorizationRequestHandlerTest {

    private AuthorizationRequestHandler testObject;

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

        assertErrorResponse(result, ErrorCode.INVALID_REQUEST);
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

        assertErrorResponse(result, ErrorCode.UNAUTHORIZED_CLIENT);
    }

    @Test
    public void handle_userIsNotLoggedIn_loginPrompt() throws Exception {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateIdToken(any())).thenThrow(new RuntimeException("message"));

        Object result = testObject.handle(req, null);

        assertTrue(result instanceof String);
        assertFalse(((String) result).isEmpty());
    }

    @Test
    public void handle_unknownUser_loginPrompt() throws Exception {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateIdToken(any())).thenThrow(new Exception("user error"));

        Object result = testObject.handle(req, null);

        assertTrue(result instanceof String);
        assertFalse(((String) result).isEmpty());
    }

    @Test
    public void handle_success_redirectWithParams() throws Exception {

        User user = new User();
        URI uri = URI.create("http://resulting.uri");
        when(testObject.tokenService.validateIdToken(any())).thenReturn(user);
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
