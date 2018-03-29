package org.matsim.webvis.files.communication;

import org.junit.Test;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedRequestTest {

    @Test(expected = RequestException.class)
    public void constructor_noAuthorizationHeader_requestException() throws RequestException {

        Request request = mock(Request.class);
        when(request.headers(AuthenticatedRequest.AUTHORIZATION)).thenReturn(null);

        new AuthenticatedRequest(request);

        fail("No authorization header should throw exception");
    }

    @Test(expected = RequestException.class)
    public void constructor_noBearer_requestException() throws RequestException {

        final String header = "not a bearer";

        Request request = mock(Request.class);
        when(request.headers(AuthenticatedRequest.AUTHORIZATION)).thenReturn(header);

        new AuthenticatedRequest(request);

        fail("No Bearer token should throw exception");
    }

    @Test
    public void constructor_correctHeader_tokenIsSet() throws RequestException {

        final String token = "some-token";
        final String header = "Bearer " + token;

        Request request = mock(Request.class);
        when(request.headers(AuthenticatedRequest.AUTHORIZATION)).thenReturn(header);

        AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(request);

        assertEquals(token, authenticatedRequest.getToken());
    }
}
