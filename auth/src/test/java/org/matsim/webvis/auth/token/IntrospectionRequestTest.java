package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.QueryParamsMap;
import spark.Request;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

    @Test(expected = InvalidInputException.class)
    public void creation_noAuthorizationHeader_exception() throws InvalidInputException {
        when(mockReq.headers(any())).thenReturn(null);

        new IntrospectionRequest(mockReq);

        fail("no Authorization header should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_noBasicAuthorization_exception() throws InvalidInputException {
        when(mockReq.headers("Authorization")).thenReturn("too many parameters");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_invalidBasicAuthorizationFormat_exception() throws InvalidInputException {
        when(mockReq.headers("Authorization")).thenReturn("Bla some");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_invalidCredentialsFormat_exception() throws InvalidInputException {
        when(mockReq.headers("Authorization")).thenReturn("Basic invalidCredentials");

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_noTokenParameter_exception() throws InvalidInputException {

        String basicCredentials = Base64.getEncoder().encodeToString("client:secret".getBytes());
        when(mockReq.headers(any())).thenReturn(basicCredentials);
        when(mockReq.queryParams(any())).thenReturn(null);

        new IntrospectionRequest(mockReq);

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test
    public void creation_allgood() throws InvalidInputException {

        String clientId = "client";
        String clientSecret = "secret";
        String token = "some-token";

        String basicCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        when(mockReq.headers(any())).thenReturn("Basic " + basicCredentials);
        Map<String, String[]> map = new HashMap<>();
        map.put("token", new String[]{token});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);
        when(mockReq.queryMap()).thenReturn(params);

        IntrospectionRequest request = new IntrospectionRequest(mockReq);

        assertEquals(clientId, request.getAuthentication().getPrincipal());
        assertEquals(clientSecret, request.getAuthentication().getCredential());
        assertEquals(token, request.getToken());
    }
}
