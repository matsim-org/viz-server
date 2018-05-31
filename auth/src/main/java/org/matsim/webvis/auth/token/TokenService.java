package org.matsim.webvis.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import org.matsim.webvis.common.database.AbstractEntity;
import org.matsim.webvis.common.service.CodedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenService {

    public static final TokenService Instance = new TokenService();

    private static final Logger logger = LogManager.getLogger();
    Algorithm algorithm;
    TokenDAO tokenDAO = new TokenDAO();
    private UserService userService = UserService.Instance;
    private RelyingPartyService relyingPartyService = RelyingPartyService.Instance;

    private TokenService() {
        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        algorithm = Algorithm.RSA512(provider.getPublicKey(), provider.getPrivateKey());
    }

    Token grantWithPassword(String username, char[] password) throws CodedException {
        User user = userService.authenticate(username, password);
        return createAccessToken(user);
    }

    Token grantWithClientCredentials(ClientCredentialsGrantRequest request) {

        PrincipalCredentialToken auth = request.getTokenRequest().getBasicAuth();
        RelyingParty relyingParty = relyingPartyService.validateRelyingParty(auth.getPrincipal(), auth.getCredential());
        return createAccessToken(relyingParty);
    }

    public Token grantAccess(User user) {
        return createAccessToken(user);
    }

    public Token createIdToken(User user) {
        return createIdToken(user, "");
    }

    public Token createIdToken(User user, String nonce) {

        if (nonce.isEmpty()) {
            return createSignedToken(user.getId());
        } else {
            Map<String, String> claims = new HashMap<>();
            claims.put("nonce", nonce);
            return createSignedToken(user.getId(), claims);
        }
    }

    public Token validateToken(String encodedToken) {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(encodedToken);

        if (StringUtils.isBlank(decodedToken.getId()) || decodedToken.getExpiresAt().before(Date.from(Instant.now())))
            throw new RuntimeException("token expired");

        Token token = tokenDAO.find(decodedToken.getId());
        if (token == null)
            throw new RuntimeException("token invalid.");
        return token;
    }

    Token findToken(String token) {
        return tokenDAO.findByTokenValue(token);
    }

    private Token createAccessToken(AbstractEntity subject) {
        return createSignedToken(subject.getId());
    }

    private Token createSignedToken(String subjectId) {
        return createSignedToken(subjectId, null);
    }

    private Token createSignedToken(String subjectId, Map<String, String> claims) {

        Token token = new Token();
        token.setSubjectId(subjectId);
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
        token = tokenDAO.persist(token);

        JWTCreator.Builder jwt = JWT.create().withSubject(subjectId)
                .withIssuer("http://this.should/be/a/proper/domain")
                .withIssuedAt(Date.from(token.getCreatedAt()))
                .withExpiresAt(Date.from(token.getExpiresAt()))
                .withJWTId(token.getId());

        if (claims != null) {
            claims.forEach(jwt::withClaim);
        }

        String tokenValue = jwt.sign(algorithm);
        token.setTokenValue(tokenValue);
        return tokenDAO.persist(token);
    }
}
