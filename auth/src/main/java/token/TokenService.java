package token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import data.entities.AccessToken;
import data.entities.RefreshToken;
import data.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class TokenService {

    private static final Logger logger = LogManager.getLogger();

    private UserService userService = new UserService();
    private TokenDAO tokenDAO = new TokenDAO();

    public AccessToken grantWithPassword(String username, char[] password) throws Exception {
        User user = userService.authenticate(username, password);
        RefreshToken refreshToken = createRefreshToken(user);

        return createAccessToken(user, refreshToken);
    }

    private AccessToken createAccessToken(User user, RefreshToken refreshToken) {
        String dummySecret = "dummy secret";
        AccessToken token = new AccessToken();
        token.setUser(user);
        token.setRefreshToken(refreshToken.getToken());

        String userId = Long.toString(user.getId());

        try {
            Algorithm algorithm = Algorithm.HMAC512(dummySecret);
            String jwt = JWT.create().withSubject(userId)
                    .withExpiresAt(new Date(token.getExpiresIn()))
                    .sign(algorithm);
            token.setToken(jwt);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return null;
        }
        return tokenDAO.persist(token);
    }

    private RefreshToken createRefreshToken(User user) {
        String dummySecret = "dummy secret";
        RefreshToken token = new RefreshToken();
        token.setUser(user);

        String userId = Long.toString(user.getId());
        try {
            Algorithm algorithm = Algorithm.HMAC512(dummySecret);
            String jwt = JWT.create().withSubject(userId)
                    .sign(algorithm);
            token.setToken(jwt);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return tokenDAO.persist(token);
    }
}
