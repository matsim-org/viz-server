package org.matsim.webvis.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpCredential;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ClientAuthentication implements HttpCredential {

    public enum AuthState {NotAuthenticated, Requesting, Failed, Authenticated}

    private static final Logger logger = LogManager.getLogger();

    @Getter
    private AuthState state = AuthState.NotAuthenticated;
    @Getter
    private String accessToken;
    @Getter
    private String scope;

    private final Http http;
    private final URI tokenEndpoint;
    private final String principal;
    private final String credential;
    private final String clientScope;

    @Override
    public String headerValue() {
        return "Bearer " + accessToken;
    }

    public void requestAccessToken() {

        state = AuthState.Requesting;
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
        formParams.add(new BasicNameValuePair("scope", clientScope));

        try {
            AccessTokenResponse response = http.post(tokenEndpoint)
                    .withCredential(BasicAuthentication.encodeToCredential(new PrincipalCredentialToken(principal, credential)))
                    .withEntityBody(new UrlEncodedFormEntity(formParams, Consts.UTF_8))
                    .executeWithJsonResponse(AccessTokenResponse.class);

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
