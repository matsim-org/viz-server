package org.matsim.viz.clientAuth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
public class OAuthIntrospectionAuthenticator<P extends Principal> implements Authenticator<String, P> {

    Client client;
    URI idProvider;
    Function<AuthenticationResult, Optional<P>> principalProvider;
    Credentials credentials;

    @Override
    public Optional<P> authenticate(String token) throws AuthenticationException {

        IntrospectionResult result = introspectToken(token);
        if (result.isActive())
            return principalProvider.apply(new AuthenticationResult(result.getSub(), result.getScope()));

        return Optional.empty();
    }

    private IntrospectionResult introspectToken(String token) throws AuthenticationException {

        try {
            return client.target(idProvider.resolve("/introspect"))
                    .queryParam("token", token)
                    .request()
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, credentials.getPrincipal())
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, credentials.getCredential())
                    .get(IntrospectionResult.class);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}
