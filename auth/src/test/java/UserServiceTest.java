import data.User;
import org.junit.Before;
import org.junit.Test;
import user.UserService;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class UserServiceTest {

    private UserService testObject;

    @Before
    public void setUp() {
        testObject = new UserService();
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsDontMatchInLength_Exception() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "passwordRepeated".toCharArray();
        final String mail = "mail";

        User result = testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsDontMatch_Exception() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final char[] passwordRepeated = "longpasswort".toCharArray();
        final String mail = "mail";

        User result = testObject.createUser(mail, password, passwordRepeated);

        fail("should throw exception when passwords don't match");
    }

    @Test(expected = Exception.class)
    public void createUser_passwordsTooShort_Exception() throws Exception {
        final char[] password = "short".toCharArray();
        final String mail = "mail";

        User result = testObject.createUser(mail, password, password);

        fail("should throw exception when password is too short");
    }

    @Test
    public void createUser_userIsCreated_user() throws Exception {
        final char[] password = "longpassword".toCharArray();
        final String mail = "mail";

        User first = testObject.createUser(mail, password, password);

        assertEquals(mail, first.getEMail());
    }

    @Test
    public void createUser_userExists_null() throws Exception {
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
}
