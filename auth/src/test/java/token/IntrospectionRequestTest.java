package token;

import org.junit.Before;
import org.junit.Test;
import requests.RequestException;
import spark.Request;

import java.util.Base64;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectionRequestTest {

    private Request mockReq;

    @Before
    public void setUp() {
        mockReq = mock(Request.class);
    }

    @Test(expected = RequestException.class)
    public void creation_noAuthorizationHeader_exception() throws RequestException {
        when(mockReq.headers(any())).thenReturn(null);

        new IntrospectionRequest(mockReq);

        fail("no Authorization header should throw requestexception");
    }

    @Test(expected = RequestException.class)
    public void creation_noBasicAuthorization_exception() throws RequestException {
        when(mockReq.headers("Authorization")).thenReturn("too many parameters");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw requestexception");
    }

    @Test(expected = RequestException.class)
    public void creation_invalidBasicAuthorizationFormat_exception() throws RequestException {
        when(mockReq.headers("Authorization")).thenReturn("Bla some");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw requestexception");
    }

    @Test(expected = RequestException.class)
    public void creation_invalidCredentialsFormat_exception() throws RequestException {
        when(mockReq.headers("Authorization")).thenReturn("Basic invalidCredentials");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw requestexception");
    }

    @Test(expected = RequestException.class)
    public void creation_noTokenParameter_exception() throws RequestException {

        String basicCredentials = Base64.getEncoder().encodeToString("client:secret".getBytes());
        when(mockReq.headers(any())).thenReturn(basicCredentials);
        when(mockReq.queryParams(any())).thenReturn(null);

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw requestexception");
    }

    @Test
    public void creation_allgood() throws RequestException {

        String clientId = "client";
        String clientSecret = "secret";
        String token = "some-token";

        String basicCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        when(mockReq.headers(any())).thenReturn("Basic " + basicCredentials);
        when(mockReq.queryParams(any())).thenReturn(token);

        IntrospectionRequest request = new IntrospectionRequest(mockReq);

        assertEquals(clientId, request.getRpId());
        assertEquals(clientSecret, request.getRpSecret());
        assertEquals(token, request.getToken());
    }
}
