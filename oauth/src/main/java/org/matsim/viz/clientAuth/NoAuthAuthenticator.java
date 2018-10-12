package org.matsim.viz.clientAuth;

import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class NoAuthAuthenticator<P extends Principal> implements Authenticator<String, P> {

    Supplier<Optional<P>> principalProvider;

    @Override
    public Optional<P> authenticate(String emptyCredentials) {
        return principalProvider.get();
    }
}
