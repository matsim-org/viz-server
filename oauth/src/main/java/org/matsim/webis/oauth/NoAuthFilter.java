package org.matsim.webis.oauth;

import io.dropwizard.auth.AuthFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import java.security.Principal;

public class NoAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {

        if (!authenticate(containerRequestContext, "", "no-credentials")) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
    }

    public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, NoAuthFilter<P>> {

        @Override
        protected NoAuthFilter<P> newInstance() {
            return new NoAuthFilter<>();
        }
    }
}
