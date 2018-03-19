package user;

import entities.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserServiceTest {

    private UserService testObject;
    private UserDAO userDAO = new UserDAO();

    @Before
    public void setUp() {
        testObject = new UserService();
    }

    @After
    public void tearDown() {
        userDAO.removeAllUsers();
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsDontMatchInLength_Exception() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "passwordRepeated".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsDontMatch_Exception() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "longpasswort".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsTooShort_Exception() throws Exception {
        final char[] password = "short".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, password);

        fail("should throw exception when password is too short");
    }

    @Test
    public void createUser_userIsCreated_user() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";

        User first = testObject.createUser(mail, password, password);

        assertEquals(mail, first.getEMail());
    }

    @Test(expected = Exception.class)
    public void createUser_userExists_exception() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";

        User first = testObject.createUser(mail, password, password);
        User second = testObject.createUser(mail, password, password);

        assertEquals(mail, first.getEMail());
        assertNull(second);
    }

    @Test
    public void createUser_twoUniqueUsers() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";
        final String mail2 = "mail2";

        User first = testObject.createUser(mail, password, password);
        User second = testObject.createUser(mail2, password, password);

        assertEquals(mail, first.getEMail());
        assertEquals(mail2, second.getEMail());
    }

    @Test(expected = Exception.class)
    public void authenticate_noUsername_Exception() throws Exception {

        final String mail = "mail";
        final char[] password = "password".toCharArray();

        testObject.authenticate(mail, password);

        fail("authenticate should have thrown exception if user is not persisted");
    }

    @Test(expected = Exception.class)
    public void authenticate_wrongPassword_Exception() throws Exception {
        final String mail = "mail";
        final char[] password = "longpassword".toCharArray();
        testObject.createUser(mail, password, password);

        testObject.authenticate(mail, "wrongpassword".toCharArray());

        fail("authenticate should have thrown excepton if password is wrong");
    }

    @Test
    public void authenticate_correct_user() throws Exception {

        final String mail = "mail";
        final char[] password = "longpassword".toCharArray();
        User user = testObject.createUser(mail, password, password);

        User authenticated = testObject.authenticate(mail, password);

        assertEquals(user.getEMail(), authenticated.getEMail());
    }
}
