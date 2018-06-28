package org.matsim.webvis.common.auth;

import org.apache.http.client.utils.URIBuilder;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpCredential;
import org.matsim.webvis.common.errorHandling.InternalException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.net.URI;
import java.net.URISyntaxException;

public class AuthenticationHandler implements Filter {
    private Http http;
    private HttpCredential credential;
    private URI introspectionEndpoint;

    public AuthenticationHandler(Http http, PrincipalCredentialToken introspectionCredential, URI introspectionEndpoint) {
        this.http = http;
        this.credential = BasicAuthentication.encodeToCredential(introspectionCredential);
        this.introspectionEndpoint = introspectionEndpoint;
    }

    @Override
    public void handle(Request request, Response response) {

        AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(request);
        AuthenticationResult result = introspectToken(authenticatedRequest.getToken());

        if (result.isActive())
            AuthenticationResult.intoRequestAttribute(request, result);
        else
            throw new UnauthorizedException("Token is invalid");
    }

    private AuthenticationResult introspectToken(String token) {
        try {
            return http.post(createRequestURI(token))
                    .withCredential(credential)
                    .executeWithJsonResponse(AuthenticationResult.class);
        } catch (UnauthorizedException e) {
            throw new InternalException("Could not authenticate at auth server");
        }
    }

    private URI createRequestURI(String token) {
        try {
            return new URIBuilder(introspectionEndpoint).setParameter("token", token).build();
        } catch (URISyntaxException e) {
            //TODO: logger.error("Could not create introspection uri", e);
            throw new InternalException("Could not create URI");
        }
    }
}
