package org.matsim.webvis.auth.authorization;

import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.QueryParamsMap;

import java.util.Map;

import static org.junit.Assert.*;

public class AuthenticationRequestTest {

    @Test(expected = InvalidInputException.class)
    public void constructor_noRedirectUri_URIException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.REDIRECT_URI);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing redirect_uri should throw exception");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_invalidRedirectUri_URIException() throws InvalidInputException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.REDIRECT_URI, "invalid uri");
        new AuthenticationRequest(params);

        fail("invalid redirect_uri should throw exception");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noSope_InvalidInputException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.SCOPE);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing scope should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noOpenIdInScope_InvalidInputException() throws InvalidInputException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.SCOPE, "some-scope");
        new AuthenticationRequest(params);

        fail("scope with no openid should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noResponseType_InvalidInputException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.RESPONSE_TYPE);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing response_type should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_invalidResponseType_InvalidInputException() throws InvalidInputException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.RESPONSE_TYPE, "invalid response type");
        new AuthenticationRequest(params);

        fail("invalid response type throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_partiallyInvalidResponseType_InvalidInputException() throws InvalidInputException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams(AuthenticationRequest.RESPONSE_TYPE, "id_token invalid_response_type");
        new AuthenticationRequest(params);

        fail("partially invalid response type throw InvalidInputException");
    }

    @Test
    public void constructor_responseTypeIdToken_Object() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token"});
        map.put(AuthenticationRequest.NONCE, new String[]{"some-nonce"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(AuthenticationRequest.Type.IdToken, request.getType());
        assertEquals(1, request.getResponseType().length);
        assertEquals(params.get(AuthenticationRequest.RESPONSE_TYPE).value(), request.getResponseType()[0]);
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_responseTypeIdTokenNoNonce_InvalidInputException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        new AuthenticationRequest(params);

        fail("missing nonce for response_type 'id_token' should throw InvalidInputException");
    }

    @Test
    public void constructor_responseTypeIdTokenToken_Object() throws InvalidInputException {

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

    @Test(expected = InvalidInputException.class)
    public void constructor_responseTypeIdTokenTokenNoNonce_InvalidInputException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.RESPONSE_TYPE, new String[]{"id_token token"});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        new AuthenticationRequest(params);

        fail("missing nonce for response_type 'id_token' should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noClientId_InvalidInputException() throws InvalidInputException {

        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.remove(AuthenticationRequest.CLIENT_ID);

        new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        fail("missing client_id should throw InvalidInputException");
    }

    @Test
    public void constructor_requiredParameters_object() throws InvalidInputException {

        QueryParamsMap params = AuthorizationTestUtils.mockQueryParams("", "");

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(AuthenticationRequest.Type.AccessToken, request.getType());
        assertEquals(1, request.getResponseType().length);
        assertEquals(params.get("response_type").value(), request.getResponseType()[0]);

        assertTrue(request.getScope().contains("openid"));

        assertEquals(params.get(AuthenticationRequest.CLIENT_ID).value(), request.getClientId());
        assertEquals(params.get(AuthenticationRequest.REDIRECT_URI).value(), request.getRedirectUri().toString());
    }

    @Test
    public void constructor_statePresent_object() throws InvalidInputException {

        final String state = "some-state-value";
        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        map.put(AuthenticationRequest.STATE, new String[]{state});

        AuthenticationRequest request = new AuthenticationRequest(TestUtils.mockQueryParamsMap(map));

        assertEquals(state, request.getState());
    }

    @Test
    public void constructor_scopesPresent_object() {
        Map<String, String[]> map = AuthorizationTestUtils.createDefaultParameterMap();
        String scope = "openid some scopes";
        map.put(AuthenticationRequest.SCOPE, new String[]{scope});
        QueryParamsMap params = TestUtils.mockQueryParamsMap(map);

        AuthenticationRequest request = new AuthenticationRequest(params);

        assertEquals(scope, request.getScope());
    }
}
