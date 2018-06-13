package org.matsim.webvis.common.auth;

import org.junit.Test;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.util.TestUtils;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ClientAuthenticationTest {

    @Test
    public void requestAccessToken_requestFails_failed() {

        HttpClientFactory factory = TestUtils.clientFactoryWithFailingHttpClient();

        final String principal = "principal";
        final String credential = "secret";
        final URI endpoint = URI.create("http://some-url.com");
        final Http http = new Http(factory);

        ClientAuthentication auth = new ClientAuthentication(http, endpoint, principal, credential, "");

        auth.requestAccessToken();

        assertEquals(ClientAuthentication.AuthState.Failed, auth.getState());
    }

    @Test
    public void requestAccessToken_allGood_allParametersSet() {

        AccessTokenResponse expectedResponse = new AccessTokenResponse("token", "token-type", "scope", 1);
        HttpClientFactory factory = TestUtils.clientFactoryWithMockedHttpClient(expectedResponse);

        final String principal = "principal";
        final String credential = "secret";
        final URI endpoint = URI.create("http://some-url.com");
        final Http http = new Http(factory);

        ClientAuthentication auth = new ClientAuthentication(http, endpoint, principal, credential, "");

        auth.requestAccessToken();

        assertEquals(ClientAuthentication.AuthState.Authenticated, auth.getState());
        assertEquals(expectedResponse.getAccess_token(), auth.getAccessToken());
        assertEquals(expectedResponse.getScope(), auth.getScope());
    }
}
