package org.matsim.webvis.auth.user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.config.ConfigUser;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserServiceTest {

    private UserService testObject;
    private UserDAO userDAO = new UserDAO();

    @Before
    public void setUp() {
        testObject = UserService.Instance;
    }

    @After
    public void tearDown() {
        userDAO.removeAllUsers();
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_passwordsDontMatchInLength_Exception() {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "passwordRepeated".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_passwordsDontMatch_Exception() {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "longpasswort".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_passwordsTooShort_Exception() {
        final char[] password = "short".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, password);

        fail("should throw exception when password is too short");
    }

    @Test
    public void createUser_userIsCreated_user() {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";

        User first = testObject.createUser(mail, password, password);

        assertEquals(mail, first.getEMail());
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_userExists_exception() {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";

        testObject.createUser(mail, password, password);
        testObject.createUser(mail, password, password);

        fail("existing user email should cause exception");
    }

    @Test
    public void createUser_twoUniqueUsers() {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";
        final String mail2 = "mail2";

        User first = testObject.createUser(mail, password, password);
        User second = testObject.createUser(mail2, password, password);

        assertEquals(mail, first.getEMail());
        assertEquals(mail2, second.getEMail());
    }

    @Test
    public void createUser_configUser_allFine() {

        ConfigUser configUser = new ConfigUser("name", "id", "longpassword");

        User result = testObject.createUser(configUser);

        assertEquals(configUser.getId(), result.getId());
        assertEquals(configUser.getUsername(), result.getEMail());
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_noUsername_Exception() {

        final String mail = "mail";
        final char[] password = "password".toCharArray();

        testObject.authenticate(mail, password);

        fail("authenticate should have thrown exception if user is not persisted");
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_wrongPassword_Exception() {
        final String mail = "mail";
        final char[] password = "longpassword".toCharArray();
        testObject.createUser(mail, password, password);

        testObject.authenticate(mail, "wrongpassword".toCharArray());

        fail("authenticate should have thrown exception if password is wrong");
    }

    @Test
    public void authenticate_correct_user() {

        final String mail = "mail";
        final char[] password = "longpassword".toCharArray();
        User user = testObject.createUser(mail, password, password);

        User authenticated = testObject.authenticate(mail, password);

        assertEquals(user.getEMail(), authenticated.getEMail());
    }

    @Test
    public void find_userIsFound() {

        User user = TestUtils.persistUser("mail", "longpassword");

        User found = testObject.findUser(user.getId());

        assertEquals(user.getEMail(), found.getEMail());
    }
}
