package org.matsim.viz.auth.token;

import com.auth0.jwt.JWT;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.util.TestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    private static TokenDAO tokenDAO = new TokenDAO();
    private TokenService testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
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

    @Test
    public void createAccessToken_token() {

        User user = TestUtils.persistUser("mail", "longpassword");
        String scope = "some-scope";

        Token token = testObject.createAccessToken(user, scope);

        assertEquals(user.getId(), token.getSubjectId());
        assertEquals(scope, token.getScope());
        assertNotNull(token.getTokenValue());
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
        Token token = testObject.createIdToken(user);

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