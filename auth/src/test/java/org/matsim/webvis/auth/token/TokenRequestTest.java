package org.matsim.webvis.auth.token;

import org.junit.Test;
import org.matsim.webvis.auth.helper.BasicAuthentication;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.when;

public class TokenRequestTest {

    @Test(expected = InvalidInputException.class)
    public void constructor_noGrantType_invalidInputException() {

        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        Request request = TestUtils.mockRequestWithQueryParamsMap(new HashMap<>(), ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);

        new TokenRequest(request);

        fail("missing grant_type should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noBasicAuth_invalidInputException() {

        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn("");

        new TokenRequest(request);

        fail("missing auth should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_invalidContentType_invalidInputException() {
        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.APPLICATION_JSON);
        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);

        new TokenRequest(request);

        fail("invalid content type should cause exception");
    }

    @Test
    public void constructor_allParametersSupplied_instance() {

        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        final String principal = "name";
        final String credential = "secret";
        final String basicAuth = TestUtils.encodeBasicAuth(principal, credential);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);

        TokenRequest tokenRequest = new TokenRequest(request);

        assertEquals(OAuthParameters.GRANT_TYPE_PASSWORD, tokenRequest.getGrantType());
        assertEquals(principal, tokenRequest.getBasicAuth().getPrincipal());
        assertEquals(credential, tokenRequest.getBasicAuth().getCredential());
    }
}
