package org.matsim.viz.clientAuth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.net.URI;

@RequiredArgsConstructor
public class ClientAuthentication {

    private static Logger logger = LoggerFactory.getLogger(ClientAuthentication.class);
    private final Client client;
    private final URI tokenEndpoint;
    private final String clientScope;
    private final Credentials credentials;
    @Getter
    private String accessToken;
    @Getter
    private String scope;

    public void requestAccessToken() {

        logger.info("requesting access token at: " + tokenEndpoint.toString());
        try {
            AccessTokenResponse response = client.target(tokenEndpoint)
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("scope", clientScope)
                    .request()
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, credentials.getPrincipal())
                    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, credentials.getCredential())
                    .post(null, AccessTokenResponse.class);
            accessToken = response.getAccess_token();
            scope = response.getScope();
        } catch (RuntimeException e) {
            logger.error("Could not retrieve access token.", e);
        }
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isEmpty();
    }
}
