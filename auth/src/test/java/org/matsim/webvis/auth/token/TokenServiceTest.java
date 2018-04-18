package org.matsim.webvis.auth.token;

import com.auth0.jwt.JWT;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.*;
import org.matsim.webvis.auth.relyingParty.RelyingPartyDAO;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.auth.util.TestUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    private static String username = "mail";
    private static char[] userpassword = "somepassword".toCharArray();
    private static User user;
    private TokenService testObject;

    @BeforeClass
    public static void setUpFixture() throws Exception {
        TestUtils.loadTestConfig();
        UserService service = new UserService();
        user = service.createUser(username, userpassword, userpassword);
    }

    @AfterClass
    public static void tearDownFixture() {

        TokenDAO tokenDAO = new TokenDAO();
        tokenDAO.removeAllTokensForUser(user);
        UserService service = new UserService();
        service.deleteUser(user);
        new RelyingPartyDAO().removeAllRelyingParties();
    }

    @Before
    public void setUp() throws Exception {
        testObject = new TokenService();
        testObject.tokenDAO = spy(new TokenDAO());
    }

    @Test
    public void grantWithPassword_allRight_accessToken() throws Exception {

        Token token = testObject.grantWithPassword(username, userpassword);

        assertEquals(user.getId(), token.getUser().getId());
        assertEquals("Bearer", token.getTokenType());
        assertTrue(token.getExpiresAt().toEpochMilli() > 0);
        assertFalse(token.getToken().isEmpty());
        assertFalse(((AccessToken) token).getRefreshToken().isEmpty());
    }

    @Test(expected = Exception.class)
    public void grantWithPassword_authenticationFails_Exception() throws Exception {

        testObject.grantWithPassword(username, "wrong password".toCharArray());

        fail("grantWithPassword should have thrown exception if password is wrong");
    }

    @Test
    public void grantAccess_withoutRefreshtoken_accessToken() throws Exception {

        Token token = testObject.grantAccess(user);

        User validated = testObject.validateIdToken(token.getToken());
        assertEquals(user.getId(), validated.getId());
        verify(testObject.tokenDAO).persist(eq(token));
    }

    @Test
    public void createIdToken_withNonce_idToken() throws Exception {

        IdToken token = testObject.createIdToken(user, "somenonce");

        verify(testObject.tokenDAO).persist(eq(token));
        User validated = testObject.validateIdToken(token.getToken());
        assertEquals(user.getId(), validated.getId());
    }

    @Test
    public void createIdToken_withoutNonce_idToken() throws Exception {

        IdToken token = testObject.createIdToken(user);

        verify(testObject.tokenDAO).persist(eq(token));
        User validated = testObject.validateIdToken(token.getToken());
        assertEquals(user.getId(), validated.getId());
    }

    @Test(expected = RuntimeException.class)
    public void validateToken_invalidToken_runtimeException() throws Exception {

        final String token = "invalid-token";

        testObject.validateIdToken(token);

        fail("invalid token should cause a runtime exception (e.g. JWTVerificationException");
    }

    @Test(expected = Exception.class)
    public void validateToken_userNotPresent_runtimeException() throws Exception {

        String token = JWT.create().withSubject("10000").sign(TokenService.algorithm);

        testObject.validateIdToken(token);

        fail("invalid user id in token should throw exception e.g. NumberFormatException");
    }

    @Test
    public void validateToken_validTokenUserPresent_user() throws Exception {

        String token = JWT.create().withSubject(user.getId()).sign(TokenService.algorithm);

        User validated = testObject.validateIdToken(token);

        assertEquals(user.getId(), validated.getId());
    }

    @Test
    public void createAuthorizationCode_allRight_Token() {

        List<URI> uris = new ArrayList<>();
        uris.add(URI.create("http://callback.uri"));
        Client client = new RelyingPartyService().createClient("some name", uris);

        AuthorizationCode token = testObject.createAuthorizationCode(user, client.getId());

        verify(testObject.tokenDAO).persist(token, client.getId());
        assertEquals(client.getId(), token.getClient().getId());
    }

    @Test
    public void findToken_noToken_null() {

        Token result = testObject.getToken("some token");

        assertNull(result);
    }

    @Test
    public void findToken_hasToken_token() {

        Token token = testObject.createIdToken(user);

        Token found = testObject.getToken(token.getToken());

        assertNotNull(found);
        assertEquals(token.getId(), token.getId());
    }

    @Test
    public void findAccessToken_noToken_null() {
        AccessToken result = testObject.findAccessToken("some-token-value");

        assertNull(result);
    }

    @Test
    public void findAccessToken_hasToken_accessToken() {
        AccessToken token = testObject.grantAccess(user);

        AccessToken found = testObject.findAccessToken(token.getToken());

        assertNotNull(found);
        assertEquals(token.getId(), found.getId());
    }
}
