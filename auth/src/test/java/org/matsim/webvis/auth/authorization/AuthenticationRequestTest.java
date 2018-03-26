package org.matsim.webvis.auth.authorization;

import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.RequestException;
import spark.QueryParamsMap;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthenticationRequestTest {

    @Test(expected = URIException.class)
    public void constructor_noRedirectUri_URIException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.REDIRECT_URI);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing redirect_uri should throw uriexception");
    }

    @Test(expected = URIException.class)
    public void constructor_invalidRedirectUri_URIException() throws URIException, RequestException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.REDIRECT_URI, "invalid uri");
        new AuthenticationRequest(params);

        fail("invalid redirect_uri should throw uriexception");
    }

    @Test(expected = RequestException.class)
    public void constructor_noSope_RequestException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.SCOPE);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing scope should throw RequestException");
    }

    @Test(expected = RequestException.class)
    public void constructor_noOpenIdInScope_RequestException() throws URIException, RequestException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.SCOPE, "some-scope");
        new AuthenticationRequest(params);

        fail("scope with no openid should throw RequestException");
    }

    @Test(expected = RequestException.class)
    public void constructor_noResponseType_RequestException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.RESPONSE_TYPE);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing response_type should throw RequestException");
    }

    @Test(expected = RequestException.class)
    public void constructor_invalidResponseType_RequestException() throws URIException, RequestException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.RESPONSE_TYPE, "invalid response type");
        new AuthenticationRequest(params);

        fail("invalid response type throw RequestException");
    }

    @Test(expected = RequestException.class)
    public void constructor_partiallyInvalidResponseType_RequestException() throws URIException, RequestException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.RESPONSE_TYPE, "id_token invalid_response_type");
        new AuthenticationRequest(params);

        fail("partially invalid response type throw RequestException");
    }

    @Test
    public void constructor_responseTypeIdToken_Object() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token"});
        map.put(AuthenticationRequest.NONCE, new String[]{"some-nonce"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(AuthenticationRequest.Type.IdToken, request.getType());
        assertEquals(1, request.getResponseType().length);
        assertEquals(params.get(AuthenticationRequest.RESPONSE_TYPE).value(), request.getResponseType()[0]);
    }

    @Test(expected = RequestException.class)
    public void constructor_responseTypeIdTokenNoNonce_RequestException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        new AuthenticationRequest(params);

        fail("missing nonce for response_type 'id_token' should throw RequestException");
    }

    @Test
    public void constructor_responseTypeIdTokenToken_Object() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token token"});
        map.put(AuthenticationRequest.NONCE, new String[]{"some-nonce"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(AuthenticationRequest.Type.AccessAndIdToken, request.getType());
        assertEquals(2, request.getResponseType().length);
        assertEquals("id_token", request.getResponseType()[0]);
        assertEquals("token", request.getResponseType()[1]);
    }

    @Test(expected = RequestException.class)
    public void constructor_responseTypeIdTokenTokenNoNonce_RequestException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token org.matsim.webvis.auth.token"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        new AuthenticationRequest(params);

        fail("missing nonce for response_type 'id_token' should throw RequestException");
    }

    @Test(expected = RequestException.class)
    public void constructor_noClientId_RequestException() throws URIException, RequestException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.CLIENT_ID);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing client_id should throw RequestException");
    }

    @Test
    public void constructor_requiredParameters_object() throws URIException, RequestException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams("", "");

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(AuthenticationRequest.Type.AuthCode, request.getType());
        assertEquals(1, request.getResponseType().length);
        assertEquals("code", request.getResponseType()[0]);

        assertEquals(1, request.getScopes().length);
        assertEquals("openid", request.getScopes()[0]);

        assertEquals(params.get(AuthenticationRequest.CLIENT_ID).value(), request.getClientId());
        assertEquals(params.get(AuthenticationRequest.REDIRECT_URI).value(), request.getRedirectUri().toString());
    }

    @Test
    public void constructor_statePresent_object() throws URIException, RequestException {

        final String state = "some-state-value";
        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.STATE, new String[]{state});

        AuthenticationRequest request = new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        assertEquals(state, request.getState());
    }


}
