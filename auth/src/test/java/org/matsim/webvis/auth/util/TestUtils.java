package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.AppConfiguration;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyDAO;
import org.matsim.webvis.auth.user.UserDAO;
import org.matsim.webvis.auth.user.UserService;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class TestUtils {

    private static UserService userService = UserService.Instance;
    private static UserDAO userDAO = new UserDAO();
    private static RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    public static HttpSession mockSession(String id) {

        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(id);
        return session;
    }

    public static void loadTestConfigIfNecessary() {

        if (AppConfiguration.getInstance() != null)
            return;

        AppConfiguration.setInstance(new TestAppConfiguration());
    }

    public static User persistUser(String eMail, String password) {
        return userService.createUser(eMail, password.toCharArray(), password.toCharArray());
    }

    public static void removeAllUser() {
        userDAO.removeAllUsers();
    }

    public static void removeAllRelyingParties() {
        relyingPartyDAO.removeAllRelyingParties();
    }
}
