package org.matsim.webvis.auth.token;

import com.auth0.jwt.JWT;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.config.ConfigRelyingParty;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    private static TokenDAO tokenDAO = new TokenDAO();
    private static RelyingPartyService rpService = RelyingPartyService.Instance;
    private TokenService testObject;

    @BeforeClass
    public static void setUpFixture() throws Exception {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = TokenService.Instance;
        testObject.tokenDAO = spy(new TokenDAO());
    }

    @After
    public void tearDown() {
        tokenDAO.removeAllTokens();
        TestUtils.removeAllRelyingParties();
        TestUtils.removeAllUser();
    }

    @Test
    public void grantWithPassword_allRight_accessToken() {

        final String password = "longpassword";
        final String name = "name";
        User user = TestUtils.persistUser(name, password);

        Token token = testObject.grantWithPassword(name, password.toCharArray());

        assertEquals(user.getId(), token.getSubjectId());
        assertTrue(token.getExpiresAt().toEpochMilli() > 0);
        assertFalse(token.getTokenValue().isEmpty());
    }

    @Test(expected = UnauthorizedException.class)
    public void grantWithPassword_authenticationFails_Exception() {

        final String password = "longpassword";
        final String name = "name";
        TestUtils.persistUser(name, password);
        testObject.grantWithPassword(name, "wrong password".toCharArray());

        fail("grantWithPassword should have thrown exception if password is wrong");
    }

    @Test(expected = UnauthorizedException.class)
    public void grantWithClientCredentials_authenticationFails_unauthorizedException() {

        final String id = "rpId";
        final String secret = "secret";
        RelyingParty party = rpService.createRelyingParty(new ConfigRelyingParty(id, "name", secret, new HashSet<>()));
        ClientCredentialsGrantRequest request = new ClientCredentialsGrantRequest(TestUtils.mockTokenRequest(id, "wrong-secret"));

        testObject.grantWithClientCredentials(request);

        fail("authentication failure should cause exception");
    }

    @Test
    public void grantWithClientCredential_allGood_Token() {

        final String id = "rpId";
        final String secret = "secret";
        RelyingParty party = rpService.createRelyingParty(new ConfigRelyingParty(id, "name", secret, new HashSet<>()));
        ClientCredentialsGrantRequest request = new ClientCredentialsGrantRequest(TestUtils.mockTokenRequest(id, secret));

        Token token = testObject.grantWithClientCredentials(request);

        assertEquals(party.getId(), token.getSubjectId());
    }

    @Test
    public void grantAccess_allGood_accessToken() {

        User user = TestUtils.persistUser("mail", "longpassword");

        Token token = testObject.grantAccess(user);

        assertEquals(user.getId(), token.getSubjectId());
        verify(testObject.tokenDAO, atLeastOnce()).persist(eq(token));
    }

    @Test
    public void createIdToken_withNonce_idToken() {

        User user = TestUtils.persistUser("mail", "longpassword");

        Token token = testObject.createIdToken(user, "somenonce");

        assertEquals(user.getId(), token.getSubjectId());
        verify(testObject.tokenDAO, atLeastOnce()).persist(eq(token));
    }

    @Test
    public void createIdToken_withoutNonce_idToken() {

        User user = TestUtils.persistUser("mail", "longpassword");

        Token token = testObject.createIdToken(user);

        assertEquals(user.getId(), token.getSubjectId());
        verify(testObject.tokenDAO, atLeastOnce()).persist(eq(token));
    }

    @Test(expected = RuntimeException.class)
    public void validateToken_invalidToken_runtimeException() {

        final String token = "invalid-token";

        testObject.validateToken(token);

        fail("invalid JWT token should throw exception");
    }

    @Test(expected = RuntimeException.class)
    public void validateToken_tokenExpired_runtimeException() {

        User user = TestUtils.persistUser("some", "longpassword");
        String token = JWT.create()
                .withSubject(user.getId())
                .withExpiresAt(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .sign(TokenService.Instance.algorithm);

        testObject.validateToken(token);

        fail("expired token should cause exception");
    }

    @Test(expected = RuntimeException.class)
    public void validateToken_noTokenIdInToken_runtimeException() {

        User user = TestUtils.persistUser("some", "longpassword");
        String token = JWT.create()
                .withSubject(user.getId())
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .sign(TokenService.Instance.algorithm);

        testObject.validateToken(token);

        fail("invalid token should cause exception");
    }

    @Test(expected = RuntimeException.class)
    public void validateToken_validTokenButNotInDatabase_runtimeException() {

        User user = TestUtils.persistUser("some", "longpassword");
        String token = JWT.create()
                .withSubject(user.getId())
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .withJWTId("invalidJWTId")
                .sign(TokenService.Instance.algorithm);

        testObject.validateToken(token);

        fail("invalid token should cause exception");
    }

    @Test
    public void validateToken_validTokenAndFound_Token() {

        User user = TestUtils.persistUser("some", "longpassword");
        Token token = testObject.grantAccess(user);

        Token result = testObject.validateToken(token.getTokenValue());

        assertEquals(token.getTokenValue(), result.getTokenValue());
    }

    @Test
    public void findToken_noToken_null() {

        Token result = testObject.findToken("some token");

        assertNull(result);
    }

    @Test
    public void findToken_hasToken_token() {

        User user = TestUtils.persistUser("some", "longpassword");
        Token token = testObject.createIdToken(user);

        Token found = testObject.findToken(token.getTokenValue());

        assertNotNull(found);
        assertEquals(token.getId(), token.getId());
    }
}
