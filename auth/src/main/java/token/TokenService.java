package token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import data.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

public class TokenService {

    private static final Logger logger = LogManager.getLogger();
    private static final String dummySecret = "dummy secret";
    private static Algorithm algorithm;

    private UserService userService = new UserService();
    private TokenDAO tokenDAO = new TokenDAO();

    public TokenService() throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC512(dummySecret);
    }

    public AccessToken grantWithPassword(String username, char[] password) throws Exception {
        User user = userService.authenticate(username, password);
        RefreshToken refreshToken = createRefreshToken(user);

        return createAccessToken(user, refreshToken);
    }

    public AccessToken grantAccess(User user) {
        return createAccessToken(user, null);
    }

    public IdToken createIdToken(User user) {
        return createIdToken(user, "");
    }

    public IdToken createIdToken(User user, String nonce) {

        IdToken token = new IdToken();
        token.setUser(user);
        token.setCreatedAt(new Date());

        String userId = Long.toString(user.getId());

        String jwt = JWT.create().withSubject(userId)
                .withClaim("nonce", nonce)
                .withIssuedAt(token.getCreatedAt())
                .sign(algorithm);
        token.setToken(jwt);
        return tokenDAO.persist(token);
    }

    public User validateIdToken(String token) {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(token);

        String userId = decodedToken.getSubject();
        return userService.findUser(Long.parseLong(userId));
    }

    public Token createAuthorizationCode(User user, String clientId) {

        AuthorizationCode code = new AuthorizationCode();
        code.setUser(user);

        String token = UUID.randomUUID().toString();
        code.setToken(token);
        return tokenDAO.persist(code, clientId);
    }

    private AccessToken createAccessToken(User user, RefreshToken refreshToken) {

        AccessToken token = new AccessToken();
        token.setUser(user);

        if (refreshToken != null)
            token.setRefreshToken(refreshToken.getToken());

        String userId = Long.toString(user.getId());

        String jwt = JWT.create().withSubject(userId)
                .withExpiresAt(new Date(token.getExpiresIn()))
                .sign(algorithm);
        token.setToken(jwt);
        return tokenDAO.persist(token);
    }

    private RefreshToken createRefreshToken(User user) {

        RefreshToken token = new RefreshToken();
        token.setUser(user);

        String userId = Long.toString(user.getId());
        String jwt = JWT.create().withSubject(userId)
                .sign(algorithm);
        token.setToken(jwt);
        return tokenDAO.persist(token);
    }
}
