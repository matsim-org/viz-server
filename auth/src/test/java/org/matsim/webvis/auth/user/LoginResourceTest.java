package org.matsim.webvis.auth.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginResourceTest {

    private LoginResource testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
    }

    @Before
    public void setUp() {

        testObject = new LoginResource();
        testObject.userService = mock(UserService.class);
        testObject.tokenService = mock(TokenService.class);
    }

    @Test
    public void login_noUsername_loginView() {

        Response response = testObject.login("", "password");

        assertEquals(Response.Status.Family.SUCCESSFUL, response.getStatusInfo().getFamily());
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof LoginResource.LoginView);
    }

    @Test
    public void login_noPassword_loginView() {

        Response response = testObject.login("username", null);

        assertEquals(Response.Status.Family.SUCCESSFUL, response.getStatusInfo().getFamily());
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof LoginResource.LoginView);
        LoginResource.LoginView view = ((LoginResource.LoginView) response.getEntity());
        assertFalse(view.isError());

    }

    @Test
    public void login_authenticationFails_loginViewWithError() {

        when(testObject.userService.authenticate(anyString(), any())).thenThrow(new UnauthorizedException(""));
        Response response = testObject.login("username", "password");

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof LoginResource.LoginView);
        LoginResource.LoginView view = ((LoginResource.LoginView) response.getEntity());
        assertTrue(view.isError());
    }

    @Test
    public void login_success_cookieSetAndRedirectToAuthorization() {

        User user = new User();
        Token token = new Token();
        token.setTokenValue("value");

        when(testObject.userService.authenticate(anyString(), any())).thenReturn(user);
        when(testObject.tokenService.createIdToken(user)).thenReturn(token);
        Response response = testObject.login("username", "password");

        assertEquals(Response.Status.Family.REDIRECTION, response.getStatusInfo().getFamily());
        assertEquals("/authorize/from-login", response.getLocation().toString());
        assertTrue(response.getCookies().containsKey("login"));
        assertEquals(token.getTokenValue(), response.getCookies().get("login").getValue());
    }

    @Test
    public void login_loginView() {

        LoginResource.LoginView view = testObject.login();

        assertFalse(view.isError());
    }
}
