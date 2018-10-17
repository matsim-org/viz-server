package org.matsim.viz.auth.util;

import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.relyingParty.RelyingPartyDAO;
import org.matsim.viz.auth.relyingParty.RelyingPartyService;
import org.matsim.viz.auth.token.TokenDAO;
import org.matsim.viz.auth.user.UserDAO;
import org.matsim.viz.auth.user.UserService;
import org.matsim.viz.database.PersistenceUnit;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class TestUtils {

    private static PersistenceUnit persistenceUnit = new PersistenceUnit("org.matsim.viz.auth");
    private static UserDAO userDAO = new UserDAO(persistenceUnit);
    private static RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO(persistenceUnit);
    private static TokenDAO tokenDAO = new TokenDAO(persistenceUnit);

    private static UserService userService = new UserService(userDAO);
    private static RelyingPartyService rpService = new RelyingPartyService(relyingPartyDAO);

    public static RelyingPartyService getRelyingPartyService() {
        return rpService;
    }

    public static PersistenceUnit getPersistenceUnit() {
        return persistenceUnit;
    }

    public static RelyingPartyDAO getRelyingPartyDAO() {
        return relyingPartyDAO;
    }

    public static HttpSession mockSession(String id) {

        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(id);
        return session;
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

    public static void removeAllTokens() {
        tokenDAO.removeAllTokens();
    }
}
