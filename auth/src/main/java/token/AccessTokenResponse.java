package token;

import data.entities.AccessToken;

public class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;

    AccessTokenResponse(AccessToken token) {
        this.access_token = token.getToken();
        this.token_type = token.getTokenType();
        this.expires_in = token.getExpiresIn();
        this.refresh_token = token.getRefreshToken();
    }
}
