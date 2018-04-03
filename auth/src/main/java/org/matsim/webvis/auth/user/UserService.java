package org.matsim.webvis.auth.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.ConfigUser;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.entities.UserCredentials;
import org.matsim.webvis.auth.helper.SecretHelper;

import javax.persistence.RollbackException;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final int minPasswordLength = 10;

    private UserDAO userDAO = new UserDAO();

    public User createUser(ConfigUser user) throws Exception {
        return createUser(user.getUsername(), user.getId(), user.getPassword().toCharArray(), user.getPassword().toCharArray());
    }

    public User createUser(String eMail, char[] password, char[] passwordRepeated) throws Exception {

        return createUser(eMail, null, password, passwordRepeated);
    }

    private User createUser(String eMail, String id, char[] password, char[] passwordRepeated) throws Exception {
        if (!isValidPassword(password)) {
            throw new Exception("password is not valid");
        }
        if (!doPasswordsMatch(password, passwordRepeated)) {
            throw new Exception("passwords dont't match");
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
            throw new Exception("user already exists");
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public User authenticate(String eMail, char[] password) throws Exception {

        UserCredentials credentials = userDAO.findUserCredentials(eMail);

        if (credentials == null) throw new Exception("username was not found.");

        String hashedPassword = SecretHelper.getEncodedSecret(password, credentials.getSalt());

        if (!SecretHelper.match(credentials.getPassword(), hashedPassword)) {
            throw new Exception("password did not match.");
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

    public User findUser(String id) {
        return userDAO.findUser(id);
    }
}
