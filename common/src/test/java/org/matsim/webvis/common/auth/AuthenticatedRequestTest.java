package org.matsim.webvis.common.auth;

import org.junit.Test;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedRequestTest {

    @Test(expected = UnauthorizedException.class)
    public void constructor_noAuthorizationHeader_InvalidInputException() throws InvalidInputException {

        Request request = mock(Request.class);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(null);

        new AuthenticatedRequest(request);

        fail("No authorization header should throw exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void constructor_noBearer_InvalidInputException() throws InvalidInputException {

        final String header = "not a bearer";

        Request request = mock(Request.class);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(header);

        new AuthenticatedRequest(request);

        fail("No Bearer token should throw exception");
    }

    @Test
    public void constructor_correctHeader_tokenIsSet() throws InvalidInputException {

        final String token = "some-token";
        final String header = "Bearer " + token;

        Request request = mock(Request.class);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(header);

        AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(request);

        assertEquals(token, authenticatedRequest.getToken());
    }
}
