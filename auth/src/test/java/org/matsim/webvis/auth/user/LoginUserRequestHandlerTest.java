package org.matsim.webvis.auth.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.service.UnauthorizedException;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class LoginUserRequestHandlerTest {

    private LoginUserRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
        //Configuration.loadConfigFile(TestUtils.getTestConfigPath(), true);
    }

    @Before
    public void setUp() {
        testObject = new LoginUserRequestHandler();
    }


    @Test
    public void handle_noUsername_promptLogin() {

        Map<String, String> map = new HashMap<>();
        map.put("password", "1234");
        Request req = TestUtils.mockRequestWithQueryParams(map, "");
        Response res = mock(Response.class);

        Object response = testObject.handle(req, res);

        assertTrue(response instanceof String);
        verify(res, never()).redirect(anyString(), anyInt());
        verify(res, never()).cookie(anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    public void handle_noPassword_promptLogin() {
        Map<String, String> map = new HashMap<>();
        map.put("username", "name");
        Request req = TestUtils.mockRequestWithQueryParams(map, "");
        Response res = mock(Response.class);

        Object response = testObject.handle(req, res);

        assertTrue(response instanceof String);
        verify(res, never()).redirect(anyString(), anyInt());
        verify(res, never()).cookie(anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    public void handle_noUsernameNoPassword_promptLogin() {
        Map<String, String> map = new HashMap<>();
        Request req = TestUtils.mockRequestWithQueryParams(map, "");
        Response res = mock(Response.class);

        Object response = testObject.handle(req, res);

        assertTrue(response instanceof String);
        verify(res, never()).redirect(anyString(), anyInt());
        verify(res, never()).cookie(anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    public void handle_failAuthentication_promptLoginWithError() {
        Map<String, String> map = new HashMap<>();
        map.put("username", "name");
        map.put("password", "1234");
        Request req = TestUtils.mockRequestWithQueryParams(map, "");
        Response res = mock(Response.class);
        testObject.userService = mock(UserService.class);
        when(testObject.userService.authenticate(any(), any())).thenThrow(new UnauthorizedException("message"));

        Object response = testObject.handle(req, res);

        assertTrue(response instanceof String);
        verify(res, never()).redirect(anyString(), anyInt());
        verify(res, never()).cookie(anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    public void handle_successfulAuthentication_setIdCookieAndRedirect() {

        Map<String, String> map = new HashMap<>();
        map.put("username", "name");
        map.put("password", "1234");
        Request req = TestUtils.mockRequestWithQueryParams(map, "");
        Response res = mock(Response.class);
        testObject.userService = mock(UserService.class);
        when(testObject.userService.authenticate(any(), any())).thenReturn(new User());
        testObject.tokenService = mock(TokenService.class);

        Token token = new Token();
        token.setTokenValue("some-token");
        when(testObject.tokenService.createIdToken(any())).thenReturn(token);

        Object response = testObject.handle(req, res);

        assertTrue(response instanceof String);
        assertEquals("OK", response);
        verify(res).redirect(eq("/authorize/"), eq(302));
        verify(res).cookie(anyString(), eq("id_token"), eq(token.getTokenValue()), anyInt(), anyBoolean(), anyBoolean());
    }
}
