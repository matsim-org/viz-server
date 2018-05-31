package org.matsim.webvis.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.communication.HttpRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ClientAuthentication {

    public enum AuthState {NotAuthenticated, Requesting, Failed, Authenticated}

    private static final Logger logger = LogManager.getLogger();

    @Getter
    private AuthState state = AuthState.NotAuthenticated;
    @Getter
    private String accessToken;
    @Getter
    private String scope;

    private final URI tokenEndpoint;
    private final String principal;
    private final String credential;
    private final HttpClientFactory httpClientFactory;

    public void requestAccessToken() {

        state = AuthState.Requesting;
        HttpPost post = new HttpPost(tokenEndpoint);
        final String auth = BasicAuthentication.encodeToAuthorizationHeader(new PrincipalCredentialToken(principal, credential));
        post.addHeader(BasicAuthentication.HEADER_AUTHORIZATION, auth);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
        post.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));

        try {
            AccessTokenResponse response = HttpRequest.withJsonResponse(post, httpClientFactory, AccessTokenResponse.class);
            accessToken = response.getAccess_token();
            scope = response.getScope();
            this.state = AuthState.Authenticated;
            logger.info("received access_token! " + accessToken);
        } catch (RuntimeException e) {
            logger.error("Error while requesting authentication ", e);
            this.state = AuthState.Failed;
        }
    }
}
