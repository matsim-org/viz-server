package org.matsim.webvis.common.auth;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.util.TestUtils;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientAuthenticationTest {

    @Test
    public void requestAccessToken_requestFails_failed() {

        HttpClientFactory factory = TestUtils.mockHttpClientFactory(401, "");

        final String principal = "principal";
        final String credential = "secret";
        final URI endpoint = URI.create("http://some-url.com");

        ClientAuthentication auth = new ClientAuthentication(endpoint, principal, credential, factory);

        auth.requestAccessToken();

        assertEquals(ClientAuthentication.AuthState.Failed, auth.getState());
    }

    @Test
    public void requestAccessToken_allGood_allParametersSet() {

        AccessTokenResponse expectedResponse = new AccessTokenResponse("token", "token-type", "scope", 1);
        HttpClientFactory factory = TestUtils.mockHttpClientFactory(200, new Gson().toJson(expectedResponse));

        final String principal = "principal";
        final String credential = "secret";
        final URI endpoint = URI.create("http://some-url.com");

        ClientAuthentication auth = new ClientAuthentication(endpoint, principal, credential, factory);

        auth.requestAccessToken();

        assertEquals(ClientAuthentication.AuthState.Authenticated, auth.getState());
        assertEquals(expectedResponse.getAccess_token(), auth.getAccessToken());
        assertEquals(expectedResponse.getScope(), auth.getScope());
    }
}
