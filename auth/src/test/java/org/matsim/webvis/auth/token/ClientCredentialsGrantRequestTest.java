package org.matsim.webvis.auth.token;

import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.auth.BasicAuthentication;
import org.matsim.webvis.common.communication.ContentType;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ClientCredentialsGrantRequestTest {

    @Test
    public void constructor_instance() {
        final String basicAuth = TestUtils.encodeBasicAuth("name", "secret");
        Map<String, String[]> map = new HashMap<>();
        map.put(OAuthParameters.GRANT_TYPE, new String[]{OAuthParameters.GRANT_TYPE_CLIENT_CREDENTIALS});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(basicAuth);
        TokenRequest tokenRequest = new TokenRequest(request);

        ClientCredentialsGrantRequest instance = new ClientCredentialsGrantRequest(tokenRequest);

        assertEquals(OAuthParameters.GRANT_TYPE_CLIENT_CREDENTIALS, instance.getTokenRequest().getGrantType());
    }
}
