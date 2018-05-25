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

public class PasswordGrantRequestTest {

    @Test(expected = InvalidInputException.class)
    public void constructor_noUsername_invalidInputException() {

        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        map.put(OAuthParameters.PASSWORD, new String[]{"password"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.APPLICATION_JSON);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);
        TokenRequest tokenRequest = new TokenRequest(request);

        new PasswordGrantRequest(tokenRequest);

        fail("missing username should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_noPassword_invalidInputException() {

        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        map.put(OAuthParameters.USERNAME, new String[]{"username"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.APPLICATION_JSON);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);
        TokenRequest tokenRequest = new TokenRequest(request);

        new PasswordGrantRequest(tokenRequest);

        fail("missing username should cause exception");
    }

    @Test
    public void constructor_allParametersSupplied_instance() {

        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        final String username = "username";
        final String password = "password";
        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_PASSWORD});
        map.put(OAuthParameters.USERNAME, new String[]{username});
        map.put(OAuthParameters.PASSWORD, new String[]{password});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.APPLICATION_JSON);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);
        TokenRequest tokenRequest = new TokenRequest(request);

        PasswordGrantRequest instance = new PasswordGrantRequest(tokenRequest);

        assertEquals(username, instance.getUsername());
        assertEquals(password, instance.getPassword());
    }
}
