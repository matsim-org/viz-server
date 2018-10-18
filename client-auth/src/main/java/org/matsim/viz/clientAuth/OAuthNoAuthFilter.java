package org.matsim.viz.clientAuth;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class OAuthNoAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    private static final String CHALLENGE_FORMAT = "%s realm=\"%s\"";

    private Supplier<Optional<P>> principalProvider;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {

        String credentials = getCredentials(containerRequestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        if (credentials == null) {
            if (!setSecurityContextWithNoAuth(containerRequestContext))
                throw new InternalServerErrorException("Could not create security context");
            else return;
        }

        if (!authenticate(containerRequestContext, credentials, SecurityContext.BASIC_AUTH))
            throw new WebApplicationException(buildUnauthorizedResponse(prefix, realm));
    }

    private boolean setSecurityContextWithNoAuth(ContainerRequestContext requestContext) {

        final Optional<P> principal = principalProvider.get();
        if (!principal.isPresent()) return false;

        final SecurityContext securityContext = requestContext.getSecurityContext();
        final boolean secure = securityContext != null && securityContext.isSecure();

        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return principal.get();
            }

            @Override
            public boolean isUserInRole(String s) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return secure;
            }

            @Override
            public String getAuthenticationScheme() {
                return "NO-AUTH";
            }
        });
        return true;
    }

    private String getCredentials(String header) {
        if (header == null) return null;

        final int space = header.indexOf(' ');
        if (space <= 0) return null;

        final String method = header.substring(0, space);
        if (!prefix.equalsIgnoreCase(method)) return null;

        return header.substring(space + 1);
    }

    private Response buildUnauthorizedResponse(String prefix, String realm) {

        return Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, String.format(CHALLENGE_FORMAT, prefix, realm))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid token"))
                .build();
    }

    public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, OAuthNoAuthFilter<P>> {

        private Supplier<Optional<P>> noAuthPrincipalProvider;

        @Override
        public OAuthNoAuthFilter<P> buildAuthFilter() {
            OAuthNoAuthFilter<P> filter = super.buildAuthFilter();
            requireNonNull(noAuthPrincipalProvider, "noAuthPrincipal provider not set");
            filter.principalProvider = noAuthPrincipalProvider;
            return filter;
        }

        @Override
        protected OAuthNoAuthFilter<P> newInstance() {
            return new OAuthNoAuthFilter<>();
        }

        public Builder<P> setNoAuthPrincipalProvider(Supplier<Optional<P>> provider) {
            this.noAuthPrincipalProvider = provider;
            return this;
        }
    }
}
