package org.matsim.viz.clientAuth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.function.Function;

public class OAuthAuthenticator<P extends Principal> implements Authenticator<String, P> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticator.class);

    private Function<AuthenticationResult, Optional<P>> principalProider;
    private JWTVerifier verifier;

    public OAuthAuthenticator(Client client, URI idProvider, Function<AuthenticationResult,
            Optional<P>> principalProvider) {

        this.principalProider = principalProvider;
        this.verifier = JWT.require(Algorithm.RSA512(new PublicKeyProvider(client, idProvider)))
                .withIssuer(idProvider.toString())
                .build();
    }

    @Override
    public Optional<P> authenticate(String token) {

        try {
            DecodedJWT decodedToken = verifier.verify(token);
            AuthenticationResult result = new AuthenticationResult(decodedToken.getSubject(), decodedToken.getClaim("scope").asString());
            logger.info("accepting authentication for subject: " + result.getSubjectId() + " with scope: " + result.getScope());
            return principalProider.apply(result);
        } catch (RuntimeException e) {
            // if anything fails, deny authentication
            logger.info("Denying authentication for token: " + token);
            return Optional.empty();
        }
    }
}
