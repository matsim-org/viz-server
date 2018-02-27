package user;

import data.entities.User;
import data.entities.UserCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.RollbackException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final SecureRandom random = new SecureRandom();
    private static final int iterations = 1024;
    private static final int keyLength = 256;
    private static final int minPasswordLength = 10;
    private static SecretKeyFactory keyFactory;

    private UserDAO userDAO = new UserDAO();

    public User createUser(String eMail, char[] password, char[] passwordRepeated) throws Exception {

        if (!isValidPassword(password)) {
            throw new Exception("password is not valid");
        }
        if (!doPasswordsMatch(password, passwordRepeated)) {
            throw new Exception("passwords dont't match");
        }
        UserCredentials credentials = createUserCredentials(password);
        User user = new User();
        user.setEMail(eMail);
        credentials.setUser(user);
        try {
            return userDAO.saveCredentials(credentials).getUser();
        } catch (RollbackException e) {
            throw new Exception("user already exists");
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
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

    private UserCredentials createUserCredentials(char[] password) throws InvalidKeySpecException {
        try {
            if (keyFactory == null) {
                keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            }
            byte[] salt = getSalt();
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = keyFactory.generateSecret(spec);
            byte[] res = key.getEncoded();

            UserCredentials credentials = new UserCredentials();
            credentials.setPassword(Base64.getEncoder().encodeToString(res));
            credentials.setSalt(salt);
            return credentials;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private byte[] getSalt() {
        byte[] result = new byte[32];
        random.nextBytes(result);
        return result;
    }
}
