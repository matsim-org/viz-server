package org.matsim.webvis.auth.user;

import org.matsim.webvis.auth.config.ConfigUser;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.entities.UserCredentials;
import org.matsim.webvis.auth.helper.SecretHelper;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.error.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.RollbackException;

public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final UserService Instance = new UserService();

    private UserService() {
    }

    private static final int minPasswordLength = 10;

    private UserDAO userDAO = new UserDAO();

    public User createUser(ConfigUser user) {
        return createUser(user.getUsername(), user.getId(), user.getPassword().toCharArray(), user.getPassword().toCharArray());
    }

    public User createUser(String eMail, char[] password, char[] passwordRepeated) {

        return createUser(eMail, null, password, passwordRepeated);
    }

    private User createUser(String eMail, String id, char[] password, char[] passwordRepeated) {
        if (!isValidPassword(password)) {
            throw new InvalidInputException("password is too short");
        }
        if (!doPasswordsMatch(password, passwordRepeated)) {
            throw new InvalidInputException("passwords don't match");
        }
        UserCredentials credentials = createUserCredentials(password);
        User user = new User();
        user.setEMail(eMail);
        user.setId(id);
        credentials.setUser(user);
        try {
            logger.info("creating user with eMail: " + user.getEMail());
            return userDAO.persistCredentials(credentials).getUser();
        } catch (RollbackException e) {
            throw new InvalidInputException("user already exists");
        } catch (Exception e) {
            logger.error("tja", e);
        }
        return null;
    }

    public User findUser(String id) {
        return userDAO.findUser(id);
    }

    User authenticate(String eMail, char[] password) {

        UserCredentials credentials = userDAO.findUserCredentials(eMail);

        if (credentials == null) throw new UnauthorizedException("username or password was wrong");

        String hashedPassword = SecretHelper.getEncodedSecret(password, credentials.getSalt());

        if (!SecretHelper.match(credentials.getPassword(), hashedPassword)) {
            throw new UnauthorizedException("username or password was wrong");
        }
        return credentials.getUser();
    }

    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    private boolean isValidPassword(char[] password) {
        return password.length >= minPasswordLength;
    }

    private boolean doPasswordsMatch(char[] password, char[] passwordRepeated) {
        if (password.length != passwordRepeated.length)
            return false;

        for (int i = 0; i < password.length; i++) {
            if (password[i] != passwordRepeated[i])
                return false;
        }
        return true;
    }

    private UserCredentials createUserCredentials(char[] password) {

        byte[] salt = SecretHelper.getRandomSalt();
        String hashedPassword = SecretHelper.getEncodedSecret(password, salt);
        UserCredentials credentials = new UserCredentials();
        credentials.setPassword(hashedPassword);
        credentials.setSalt(salt);
        return credentials;
    }
}
