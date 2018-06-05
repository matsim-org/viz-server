package org.matsim.webvis.common.auth;

import org.junit.Test;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.errorHandling.InternalException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Request;
import spark.Response;

import java.net.URI;

import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthenticationHandlerTest {

    @Test(expected = UnauthorizedException.class)
    public void handle_noAuthentication_unauthorizedException() {

        AuthenticationHandler testObject = new AuthenticationHandler(null, null, null);

        Request request = mock(Request.class);
        when(request.headers(anyString())).thenReturn(null);
        Response response = mock(Response.class);

        testObject.handle(request, response);

        fail("Halt exception expected for invalid Bearer token");
    }

    @Test(expected = InternalException.class)
    public void handle_authenticationAtAuthServerFailed_internalException() {

        Http.RequestExecutor executor = mock(Http.RequestExecutor.class);
        when(executor.withCredential(any())).thenReturn(executor);
        when(executor.executeWithJsonResponse(any())).thenThrow(new UnauthorizedException("unauthorized"));

        Http http = mock(Http.class);
        when(http.post(any())).thenReturn(executor);

        AuthenticationHandler testObject = new AuthenticationHandler(http,
                new PrincipalCredentialToken("p", "s"), URI.create("http://some.uri"));

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        fail("Halt exception expected if introspection call to auth-server doesn't succeed.");
    }

    @Test(expected = UnauthorizedException.class)
    public void handle_introspectionIsNotActive_unauthorizedException() {

        AuthenticationResult result = AuthenticationResult.create(false);
        Http.RequestExecutor executor = mock(Http.RequestExecutor.class);
        when(executor.withCredential(any())).thenReturn(executor);
        when(executor.executeWithJsonResponse(any())).thenReturn(result);

        Http http = mock(Http.class);
        when(http.post(any())).thenReturn(executor);

        AuthenticationHandler testObject = new AuthenticationHandler(http,
                new PrincipalCredentialToken("p", "s"), URI.create("http://some.uri"));

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        fail("inactive token should result in a halt exception");
    }

    @Test
    public void handle_activeToken_authenticationResultAsAttribute() {

        AuthenticationResult result = AuthenticationResult.create(true);
        Http.RequestExecutor executor = mock(Http.RequestExecutor.class);
        when(executor.withCredential(any())).thenReturn(executor);
        when(executor.executeWithJsonResponse(any())).thenReturn(result);

        Http http = mock(Http.class);
        when(http.post(any())).thenReturn(executor);

        AuthenticationHandler testObject = new AuthenticationHandler(http,
                new PrincipalCredentialToken("p", "s"), URI.create("http://some.uri"));

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(request).attribute(eq("subject"), any());
    }
}
