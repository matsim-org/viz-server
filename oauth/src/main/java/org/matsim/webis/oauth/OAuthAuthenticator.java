package org.matsim.webis.oauth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
public class OAuthAuthenticator<P extends Principal> implements Authenticator<String, P> {

    Client client;
    URI introspectionEndpoint;
    Function<String, Optional<P>> principalProvider;

    @Override
    public Optional<P> authenticate(String token) throws AuthenticationException {

        IntrospectionResult result = introspectToken(token);
        if (result.isActive())
            return principalProvider.apply(result.getSub());

        return Optional.empty();
    }

    private IntrospectionResult introspectToken(String token) throws AuthenticationException {

        try {
            return client.target(introspectionEndpoint).queryParam("token", token).request().get(IntrospectionResult.class);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}
