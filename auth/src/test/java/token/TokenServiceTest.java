package token;

import data.entities.AccessToken;
import data.entities.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import user.UserService;

import static org.junit.Assert.*;

public class TokenServiceTest {

    private TokenService testObject;
    private static String username = "mail";
    private static char[] userpassword = "somepassword".toCharArray();
    private static User user;

    @BeforeClass
    public static void setUpFixture() throws Exception {
        UserService servivce = new UserService();
        user = servivce.createUser(username, userpassword, userpassword);
    }

    @Before
    public void setUp() {
        testObject = new TokenService();
    }

    @AfterClass
    public static void tearDownFixture() {

        TokenDAO tokenDAO = new TokenDAO();
        tokenDAO.removeAllTokensForUser(user);
        UserService service = new UserService();
        service.deleteUser(user);
    }

    @Test
    public void grantWithPassword_allRight_accessToken() throws Exception {

        AccessToken token = testObject.grantWithPassword(username, userpassword);

        assertEquals(user.getId(), token.getUser().getId());
        assertEquals("Bearer", token.getTokenType());
        assertTrue(token.getExpiresIn() > 0);
        assertFalse(token.getToken().isEmpty());
        assertFalse(token.getRefreshToken().isEmpty());
    }

    @Test(expected = Exception.class)
    public void grantWithPassword_authenticationFails_Exception() throws Exception {

        testObject.grantWithPassword(username, "wrong password".toCharArray());

        fail("grantWithPassword should have thrown exception if password is wrong");
    }
}
