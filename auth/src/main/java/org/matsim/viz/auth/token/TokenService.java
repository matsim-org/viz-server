package org.matsim.viz.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.relyingParty.RelyingPartyService;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.error.UnauthorizedException;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenService {

    private final TokenDAO tokenDAO;
    private final RelyingPartyService relyingPartyService;
    private final Algorithm signingAlgorithm;
    private final URI host;

    public TokenService(TokenDAO tokenDAO, TokenSigningKeyProvider tokenSigningKeyProvider, RelyingPartyService relyingPartyService,
                        URI host) {
        this.tokenDAO = tokenDAO;
        this.relyingPartyService = relyingPartyService;
        this.signingAlgorithm = Algorithm.RSA512(tokenSigningKeyProvider);
        this.host = host;
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
                .withIssuer(host.toString())
                .withIssuedAt(Date.from(token.getCreatedAt()))
                .withExpiresAt(Date.from(token.getExpiresAt()))
                .withClaim("scope", scope)
                .withJWTId(token.getId());

        if (claims != null) {
            claims.forEach(jwt::withClaim);
        }

        String tokenValue = jwt.sign(signingAlgorithm);
        token.setTokenValue(tokenValue);

        return tokenDAO.persist(token);
    }

    public Token validateToken(String encodedToken) {

        DecodedJWT decodedToken;
        try {
            decodedToken = JWT.decode(encodedToken);
            JWT.require(signingAlgorithm).build().verify(encodedToken);
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
