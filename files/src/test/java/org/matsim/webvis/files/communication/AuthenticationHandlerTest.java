package org.matsim.webvis.files.communication;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthenticationHandlerTest {

    private TestableAuthenticationHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableAuthenticationHandler();
    }

    @Test
    public void handle_noAuthorization_halt() {

        Request request = mock(Request.class);
        when(request.headers(anyString())).thenReturn(null);

        Response response = mock(Response.class);

        try {
            testObject.handle(request, response);
            fail("Halt exception expected for invalid Bearer token");
        } catch (HaltException e) {
            verify(response).type("application/json");
            verify(response).header("WWW-Authenticate", "Bearer");
            assertEquals(HttpStatus.SC_UNAUTHORIZED, e.statusCode());
        }
    }

    @Test
    public void handle_authenticationAtAuthServerFailed_halt() throws IOException {

        StatusLine mockedStatusLine = mock(StatusLine.class);
        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);

        CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(testObject.mockedHttpClient.execute(any())).thenReturn(mockedResponse);

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        try {
            testObject.handle(request, response);
            fail("Halt exception expected if introspection call to auth-server doesn't succeed.");
        } catch (HaltException e) {
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.statusCode());
        }
    }

    @Test
    public void handle_introspectionIsNotActive_haltUnauthorized() throws IOException {

        StatusLine mockedStatusLine = mock(StatusLine.class);
        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(mockedResponse.getEntity()).thenReturn(new StringEntity("{\"active\": false}"));
        when(testObject.mockedHttpClient.execute(any())).thenReturn(mockedResponse);

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        try {
            testObject.handle(request, response);
            fail("inactive token should result in a halt exception");
        } catch (HaltException e) {
            assertEquals(HttpStatus.SC_UNAUTHORIZED, e.statusCode());
        }
    }

    @Test
    public void handle_activeToken_authenticationResultAsAttribute() throws IOException {

        StatusLine mockedStatusLine = mock(StatusLine.class);
        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(mockedResponse.getEntity()).thenReturn(new StringEntity("{\"active\": true}"));
        when(testObject.mockedHttpClient.execute(any())).thenReturn(mockedResponse);

        Request request = mock(Request.class);
        when(request.headers("Authorization")).thenReturn("Bearer some-token");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(testObject.mockedHttpClient).
                execute(argThat(post ->
                        post.getFirstHeader("Authorization") != null));

        verify(request).attribute(eq("subject"), any());
    }

    private class TestableAuthenticationHandler extends AuthenticationHandler {

        CloseableHttpClient mockedHttpClient = mock(CloseableHttpClient.class);

        TestableAuthenticationHandler() {
            super(AuthenticationHandler.builder()
                    .setIntrospectionEndpoint(URI.create("https://endpoint"))
                    .setRelyingPartyId("id")
                    .setRelyingPartySecret("secret")
                    .setTrustStore(Paths.get("./"))
                    .setTrustStorePassword("password".toCharArray()));
        }

        @Override
        void initializeSSL(Path trustStore, char[] password) {

            //don't set up any ssl context for testing
        }

        @Override
        CloseableHttpClient createHttpClient() {
            return mockedHttpClient;
        }
    }
}
