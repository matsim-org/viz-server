package org.matsim.webvis.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.entities.*;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.service.CodedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenService {

    private static final Logger logger = LogManager.getLogger();
    static Algorithm algorithm;
    TokenDAO tokenDAO = new TokenDAO();
    private UserService userService = new UserService();

    public TokenService() throws Exception {
        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        algorithm = Algorithm.RSA512(provider.getPublicKey(), provider.getPrivateKey());
    }

    AccessToken grantWithPassword(String username, char[] password) throws CodedException {
        User user = userService.authenticate(username, password);
        Token refreshToken = createRefreshToken(user);

        return createAccessToken(user, (RefreshToken) refreshToken);
    }

    public AccessToken grantAccess(User user) {
        return createAccessToken(user, null);
    }

    public IdToken createIdToken(User user) {
        return createIdToken(user, "");
    }

    public IdToken createIdToken(User user, String nonce) {

        if (nonce.isEmpty()) {
            return createSignedToken(user, new IdToken());
        } else {
            Map<String, String> claims = new HashMap<>();
            claims.put("nonce", nonce);
            return createSignedToken(user, new IdToken(), claims);
        }
    }

    public User validateIdToken(String token) throws Exception {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(token);

        User user = userService.findUser(decodedToken.getSubject());

        if (user == null) throw new Exception("user doesn't exist");
        return user;
    }

    public Token getToken(String token) {
        return tokenDAO.find(token);
    }

    AccessToken findAccessToken(String tokenValue) {
        return tokenDAO.findAccessToken(tokenValue);
    }

    public AuthorizationCode createAuthorizationCode(User user, String clientId) {

        AuthorizationCode code = new AuthorizationCode();
        code.setUser(user);

        String token = UUID.randomUUID().toString();
        code.setToken(token);
        return tokenDAO.persist(code, clientId);
    }

    private AccessToken createAccessToken(User user, RefreshToken refreshToken) {

        AccessToken token = new AccessToken();

        if (refreshToken != null) {
            token.setRefreshToken(refreshToken.getToken());
        }

        return createSignedToken(user, token);
    }

    private RefreshToken createRefreshToken(User user) {

        return createSignedToken(user, new RefreshToken());
    }

    private <T extends Token> T createSignedToken(User user, T token) {
        return createSignedToken(user, token, null);
    }

    private <T extends Token> T createSignedToken(User user, T token, Map<String, String> claims) {
        token.setUser(user);
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));

        JWTCreator.Builder jwt = JWT.create().withSubject(user.getId())
                .withIssuer("http://this.should/be/a/proper/domain")
                .withIssuedAt(Date.from(token.getCreatedAt()))
                .withExpiresAt(Date.from(token.getExpiresAt()));

        if (claims != null) {
            claims.forEach(jwt::withClaim);
        }

        String tokenValue = jwt.sign(algorithm);
        token.setToken(tokenValue);
        return tokenDAO.persist(token);
    }
}
