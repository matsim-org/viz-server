package org.matsim.viz.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.relyingParty.RelyingPartyService;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.error.UnauthorizedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenService {

    public static final TokenService Instance = new TokenService();

    Algorithm algorithm;
    TokenDAO tokenDAO = new TokenDAO();
    private RelyingPartyService relyingPartyService = RelyingPartyService.Instance;

    private TokenService() {
        TokenSigningKeyProvider provider = new TokenSigningKeyProvider();
        algorithm = Algorithm.RSA512(provider.getPublicKey(), provider.getPrivateKey());
    }

    Token grantForScope(RelyingParty relyingParty, String scope) {

        relyingPartyService.validateRelyingPartyScope(relyingParty, scope);
        return createAccessToken(relyingParty, scope);
    }

    public Token createIdToken(User user) {
        return createIdToken(user, "");
    }

    public Token createIdToken(User user, String nonce) {

        Map<String, String> claims = null;
        if (StringUtils.isNotBlank(nonce)) {
            claims = new HashMap<>();
            claims.put("nonce", nonce);
        }
        return createSignedToken(user.getId(), null, claims);
    }

    public Token createAccessToken(AbstractEntity subject, String scope) {
        return createSignedToken(subject.getId(), scope);
    }

    private Token createSignedToken(String subjectId, String scope) {
        return createSignedToken(subjectId, scope, null);
    }

    private Token createSignedToken(String subjectId, String scope, Map<String, String> claims) {

        Token token = new Token();
        token.setSubjectId(subjectId);
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
        token.setScope(scope);
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

    public Token validateToken(String encodedToken) {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken;
        try {
            decodedToken = verifier.verify(encodedToken);
        } catch (RuntimeException e) {
            throw new UnauthorizedException("invalid token");
        }

        if (StringUtils.isBlank(decodedToken.getId()) || decodedToken.getExpiresAt().before(Date.from(Instant.now())))
            throw new UnauthorizedException("token expired");

        Token token = tokenDAO.find(decodedToken.getId());
        if (token == null)
            throw new UnauthorizedException("token invalid.");
        return token;
    }

    Token findToken(String token) {
        return tokenDAO.findByTokenValue(token);
    }
}
